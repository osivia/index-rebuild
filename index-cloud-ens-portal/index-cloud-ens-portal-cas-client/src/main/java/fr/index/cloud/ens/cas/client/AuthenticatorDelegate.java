package fr.index.cloud.ens.cas.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.tomcat.CasRealm;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Version-agnostic authenticator which encapsulates the core CAS workflow of
 * redirecting to CAS for unauthenticated sessions and validating service tickets
 * when found in the request.  Implementations of the Tomcat <code>Authenticator</code>
 * class are expected to be thin wrappers that delegate most if not all authentication
 * logic to this class.
 * 
 * Add external SSO associated to casName parameter (jssteux)
 *
 * @author Marvin S. Addison
 * @version $Revision$
 * @since 3.1.12
 *
 */
public final class AuthenticatorDelegate {

    /** Log instance */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String serviceUrl;

    private String serverName;

    private String casServerLoginUrl;

    private String artifactParameterName;

    private String serviceParameterName;

    private TicketValidator ticketValidator;

    private CasRealm realm;

    /**
     * Performs CAS authentication on the given request and returns the principal
     * determined by the configured {@link CasRealm} on success.
     *
     * @param request HTTP request.
     * @param response HTTP response.
     *
     * @return The authenticated principal on authentication success, otherwise
     * null.  In the case where authentication explicitly fails, either due to
     * ticket validation failure or realm authentication failure, a 403 status
     * code is set on the response.  In cases where no existing CAS session exists,
     * a 302 redirect is set on the response to redirect to the CAS server for
     * authentication.
     */
    public final Principal authenticate(final HttpServletRequest request, final HttpServletResponse response) {
        Assertion assertion = null;
        HttpSession session = request.getSession();
        if (session != null) {
            assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        }
        if (assertion == null) {
            logger.debug("CAS assertion not found in session -- authentication required.");
            final String token = request.getParameter(this.artifactParameterName);
            final String service = CommonUtils.constructServiceUrl(request, response, this.serviceUrl, this.serverName,
                    this.serviceParameterName, this.artifactParameterName, true);
            if (CommonUtils.isBlank(token)) {
                final String redirectUrl = CommonUtils.constructRedirectUrl(this.casServerLoginUrl,
                        this.serviceParameterName, service, false, false, null);
                logger.debug("Redirecting to {}", redirectUrl);
                CommonUtils.sendRedirect(response, redirectUrl);
                return null;
            }
            
            

            
            
            try {
                logger.debug("Attempting to validate {} for {}", token, service);
                assertion = this.ticketValidator.validate(token,  service);
                logger.debug("CAS authentication succeeded.");
                if (session == null) {
                    session = request.getSession(true);
                }
                session.setAttribute(AbstractCasFilter.CONST_CAS_ASSERTION, assertion);
            } catch (final TicketValidationException e) {
                setUnauthorized(response, e.getMessage());
                return null;
            }
        }
        final Principal p = realm.authenticate(assertion.getPrincipal());
        if (p == null) {
            logger.debug("{} failed to authenticate to {}", assertion.getPrincipal().getName(), realm);
            setUnauthorized(response, null);
        }
        return p;
    }
    
    
    /**
     * performs cas redirection by preserving casName parameter
     * 
     * @param request
     * @param response
     */
    public final void performExternalSSORedirection(final HttpServletRequest request, final HttpServletResponse response) { 
        final String service = CommonUtils.constructServiceUrl(request, response, this.serviceUrl, this.serverName,
                this.serviceParameterName, this.artifactParameterName, true);
        
        if(request.getParameter("casName") != null && request.getParameter("ticket") != null) {
            String redirectUrl = CommonUtils.constructRedirectUrl(this.casServerLoginUrl,
                    this.serviceParameterName, service, false, false, null);
            String encodeCasName;
            try {
                encodeCasName = URLEncoder.encode(request.getParameter("casName").toString(), "UTF-8");
                redirectUrl += "&casName=" + encodeCasName + "&ticket=" + request.getParameter("ticket");

                // Remove casName from service
                redirectUrl = redirectUrl.replace("%3FcasName%3D"+request.getParameter("casName"),"");
                //Remove r=1 (add during logout phase)
                redirectUrl = redirectUrl.replace("%26r%3D1","");

                
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            CommonUtils.sendRedirect(response, redirectUrl);
        }         
    }


    /**
     * @return the serviceUrl
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * @param serviceUrl the serviceUrl to set
     */
    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the casServerLoginUrl
     */
    public String getCasServerLoginUrl() {
        return casServerLoginUrl;
    }

    /**
     * @param casServerLoginUrl the casServerLoginUrl to set
     */
    public void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    /**
     * @param artifactParameterName the artifactParameterName to set
     */
    public void setArtifactParameterName(final String artifactParameterName) {
        this.artifactParameterName = artifactParameterName;
    }

    /**
     * @param serviceParameterName the serviceParameterName to set
     */
    public void setServiceParameterName(final String serviceParameterName) {
        this.serviceParameterName = serviceParameterName;
    }

    /**
     * @param ticketValidator the ticketValidator to set
     */
    public void setTicketValidator(final TicketValidator ticketValidator) {
        this.ticketValidator = ticketValidator;
    }

    /**
     * @param realm the realm to set
     */
    public void setRealm(final CasRealm realm) {
        this.realm = realm;
    }

    private void setUnauthorized(final HttpServletResponse response, final String message) {
        try {
            if (message != null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Error setting 403 status.", e);
        }
    }
}