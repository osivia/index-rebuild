package fr.index.cloud.ens.cas.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.jasig.cas.client.tomcat.v85.ProxyCallbackValve;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;

/**
 *  Authenticator that handles the CAS 2.0 protocol.
 *  
 *  Add external SSO associated to casName parameter (jssteux).
 *  A new Authenticator delegate has been created to perform casName propagation to Cas Server 
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1.12
 */
public final class Cas20CasAuthenticator extends AbstractCasAuthenticator {

    public static final String AUTH_METHOD = "CAS20";

    private static final String NAME = Cas20CasAuthenticator.class.getName();

    private Cas20ServiceTicketValidator ticketValidator;

    @Override
    protected TicketValidator getTicketValidator() {
        return this.ticketValidator;
    }

    @Override
    protected String getAuthenticationMethod() {
        return AUTH_METHOD; 
    }

    @Override
    protected String getName() {
        return NAME;
    }

    /** {@inheritDoc} */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        
        
        // External SSO
        if(request.getParameter("casName") != null && request.getParameter("ticket") != null) {
            
            
            // Avoid loops if logout has failed
            if( !"1".equals(request.getParameter("r"))){
                
            
                Principal principal = null;
    
                Session session = request.getSessionInternal(false);
                if (session != null) {
                    principal = session.getPrincipal();
                }
    
                
                if( principal != null)  {
                    String targetUrl = request.getRequestURI().toString()+"?casName="+request.getParameter("casName")+"&ticket="+request.getParameter("ticket");
                    targetUrl += "&r=1";
                    targetUrl = URLEncoder.encode(targetUrl, "UTF-8");
                    String redirectUrl = "/portal/content/idx/DEFAULT_LOGOUT?redirection="+targetUrl;
                    CommonUtils.sendRedirect(response, redirectUrl);
                    return ;
                }
            }
            
            
            // Perform cas redirection even if ressource is not protected
            // The standard CAS Valve protects only protected ressources
            delegate.performExternalSSORedirection(request, response);
            
 
                
            return;
            
            

        }
        
        super.invoke(request, response);
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        this.ticketValidator = new Cas20ServiceTicketValidator(getCasServerUrlPrefix());
        if (getEncoding() != null) {
            this.ticketValidator.setEncoding(getEncoding());
        }
        this.ticketValidator.setProxyCallbackUrl(getProxyCallbackUrl());
        this.ticketValidator.setProxyGrantingTicketStorage(ProxyCallbackValve.getProxyGrantingTicketStorage());
        this.ticketValidator.setRenew(isRenew());
    }
}
