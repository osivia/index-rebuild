package fr.index.cloud.oauth.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;

import fr.index.cloud.ens.ws.nuxeo.NuxeoDrive;
import fr.index.cloud.oauth.tokenStore.PortalAuthorizationCodeStore;

/**
 * the Accept : application/json must be specified in order to reply correct JSON format
 * 
 * It's due to oAuth2 librairies ...
 * for exemple, the DefaultOAuth2ExceptionRenderer.class won't reply an applicative code
 */
public class CheckHeaderCompatibility extends HttpServletRequestWrapper {

    private static final String ACCEPT = "Accept";

    private static final String AUTHORIZATION = "Authorization";

    private static final String X_AUTHENTICATION_TOKEN = "x-authentication-token";

    /** The logger. */
    protected static Log logger = LogFactory.getLog(CheckHeaderCompatibility.class);

    private String defaultContentType = null;

    private boolean driveAuthorizationChecked = false;
    private String  OAuth2AuthorizationHeader;

    public static ApplicationContext appContext;

    public CheckHeaderCompatibility(HttpServletRequest request, String defaultContentType) {
        super(request);
        this.defaultContentType = defaultContentType;
    }

    /**
     * The default behavior of this method is to return getHeaders(String name)
     * on the wrapped request object.
     */
    public Enumeration getHeaders(String name) {


        Enumeration e = super.getHeaders(name);
        if (ACCEPT.equals(name)) {

            boolean mustTransform = false;


            if (e != null && e.hasMoreElements()) {
                String value = (String) e.nextElement();
                if ("*/*".equals(value) && !e.hasMoreElements()) {
                    mustTransform = true;
                }
            } else {
                mustTransform = true;
            }


            if (mustTransform) {
                if (logger.isDebugEnabled()) {
                    if (defaultContentType != null)
                        logger.debug("add " + defaultContentType + " to header");
                }

                List<String> values = new ArrayList<String>();
                if (defaultContentType != null) {
                    values.add(defaultContentType);
                    return Collections.enumeration(values);

                }
            }
        }

        if (AUTHORIZATION.equalsIgnoreCase(name) && (e == null || !e.hasMoreElements())) {
            String driveAuthorization = getOAuth2AuthorizationHeader();
            if (!StringUtils.isEmpty(driveAuthorization)) {
                List<String> values = new ArrayList<String>();
                values.add(driveAuthorization);
                return Collections.enumeration(values);
            }
        }


        return super.getHeaders(name);
    }

    /**
     * The default behavior of this method is to return getHeader(String name)
     * on the wrapped request object.
     */
    public String getHeader(String name) {
        
        String value = super.getHeader(name);

        if (ACCEPT.equals(name)) {
            if (value == null || ("*/*".equals(value))) {
                if (defaultContentType != null)
                    value = defaultContentType;
            }
        }

        if (AUTHORIZATION.equalsIgnoreCase(name) && StringUtils.isEmpty(value)) {
            value = getOAuth2AuthorizationHeader();
        }

        return value;
    }

    private String getOAuth2AuthorizationHeader() {
        
        if (driveAuthorizationChecked == false) {

            // Nuxeo Drive token
            String driveAuthorization = super.getHeader(X_AUTHENTICATION_TOKEN);

            if (driveAuthorization != null) {
                List<String> scopes =  new ArrayList<String>();
                scopes.add(NuxeoDrive.NUXEO_DRIVE_SCOPE);
                
                TokenRequest tokenRequest = new TokenRequest(new HashMap<>(), NuxeoDrive.NUXEO_DRIVE_CLIENT_ID,scopes, "implicit");
                DefaultTokenServices tokenServices = appContext.getBean("defaultAuthorizationServerTokenServices", DefaultTokenServices.class);
                
                try {

                OAuth2AccessToken accessToken = tokenServices.refreshAccessToken(driveAuthorization, tokenRequest);
                
                //System.out.println("refresh "+driveAuthorization+ " access=" + accessToken);
                
                OAuth2AuthorizationHeader = "Bearer " + accessToken;
                } catch (Exception e) {
                    OAuth2AuthorizationHeader = null;
                    
                    // Unvalidate nuxeo drive session
                    getSession().invalidate();
                }
                
            } else {
                OAuth2AuthorizationHeader = null;
            }
            
            driveAuthorizationChecked = true;
        }

        return OAuth2AuthorizationHeader;

    }


    /**
     * The default behavior of this method is to return getHeaderNames()
     * on the wrapped request object.
     */

    public Enumeration getHeaderNames() {
        
        List<String> names = Collections.list(super.getHeaderNames());

        if (!names.contains(ACCEPT))
            names.add(ACCEPT);
        if (!names.contains(AUTHORIZATION)) {
            String driveAuthorization = getOAuth2AuthorizationHeader();
            if (!StringUtils.isEmpty(driveAuthorization)) {
                names.add(AUTHORIZATION);
            }
        }
        return Collections.enumeration(names);
    }


}