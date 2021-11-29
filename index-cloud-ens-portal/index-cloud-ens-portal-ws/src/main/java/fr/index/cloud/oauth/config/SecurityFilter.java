package fr.index.cloud.oauth.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.portlet.AnnotationPortletApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import fr.index.cloud.ens.ws.portlet.WSUtilPortlet;
import fr.index.cloud.oauth.tokenStore.PortalAuthorizationCodeStore;

/**
 * Implements web security
 * - cors
 * - session fixation
 * 
 * @author Jean-Sébastien
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityFilter implements Filter {

    /** The logger. */
    protected static Log logger = LogFactory.getLog(SecurityFilter.class);
    
    private static Pattern accessTokenPattern = Pattern.compile("Bearer ([^_]*)_([^_]*)");

    /**
     * Extract session id from Bearer
     *
     * @param bearer the bearer
     * @return the string
     */
    private static String extractOAuth2Id(HttpServletRequest request) {
        String authorization = request.getHeader("authorization");
        if (authorization != null) {
           Matcher m = accessTokenPattern.matcher(authorization);
            if (m.matches()) {
                int nb = m.groupCount();
                if (nb == 2)
                    return m.group(2);
            }
        }
        return null;
    }

    public FilterConfig filterConfig;


    public SecurityFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, content-type");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            

            
            // Default content type (web-services)
            String defaultContentType = "application/json";
            
            
            /*
             * Preserve http session security
             * - by state
             * - if no state, by timeout
             */
            

            if( request.getRequestURI().endsWith("/oauth/authorize"))    {
                defaultContentType = "text/html";
                
                // /oauth/authorize must be accessed just one time par login
                // if not, the user must reauthenticate for security reason (ie. the browser is kept opened)
                // Thus we compare the state parameter with the stored state
                
                String requestState = (String) req.getParameter("state");
                
                if (StringUtils.isNotEmpty(requestState)) {
                    String sessionState = (String) ((HttpServletRequest) req).getSession(true).getAttribute("oauth2.state");
                    if (!StringUtils.equals(requestState, sessionState)) {
                        ((HttpServletRequest) req).getSession(false).invalidate();
                    }
                } else {
                    // Keep sure that session is not opened since more than 2 minutes
                    // (in case the browser keeps opened)
                    Long sessionTs = (Long) ((HttpServletRequest) req).getSession(true).getAttribute("oauth2.sessionTs");
                    if (sessionTs != null) {
                        if (System.currentTimeMillis() - sessionTs > 120000) {
                            ((HttpServletRequest) req).getSession(false).invalidate();
                        }
                    }
                }
                
                // Store the request state
                ((HttpServletRequest) req).getSession(true).setAttribute("oauth2.state", requestState);

                // Store the ts 
                ((HttpServletRequest) req).getSession(true).setAttribute("oauth2.sessionTs", System.currentTimeMillis());                
             }      
            
             
            
            
            // Debug headers
            
            if (logger.isDebugEnabled()) {
                Enumeration headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String name = (String) headerNames.nextElement();
                    String value = request.getHeader(name);
                    logger.debug("header " + name + "=" + value);
                }
            }
            
            
            
            checkSynchronizationSessionWithOAuth2(request);


            try {

                ClusterInfo.instance.set(new ClusterInfo(request));
                chain.doFilter(new CheckHeaderCompatibility(request, defaultContentType), res);
            } finally {
                ClusterInfo.instance.set(null);
            }
            
        }
        

     }

    /**
     * Synchronize Auth2 Bearer and local session portal session
     * 
     * The ACCESS_TOKEN contains the SESSIONID 
     *
     * @param request the request
     */
    
    
    public static void checkSynchronizationSessionWithOAuth2(HttpServletRequest request) {

        String sessionId = extractOAuth2Id(request);

        if (sessionId != null) {
            HttpSession session = request.getSession(false);
            

            if (logger.isDebugEnabled()) {
                if (session == null) {
                    logger.debug("before session is null");
                } else {
                    logger.debug("before session =" + session.getId());
                }
            }

            if (session == null)
                logger.warn("OAuth2 session is null");
            else {
                if (!session.getId().equals(sessionId)) {
                    logger.warn("OAuth2 session is not synchronized  " + session.getId() + " instead of" + sessionId);
                }
            }
        }

    }


    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        WSUtilPortlet.servletContext = filterConfig.getServletContext();
    }
}