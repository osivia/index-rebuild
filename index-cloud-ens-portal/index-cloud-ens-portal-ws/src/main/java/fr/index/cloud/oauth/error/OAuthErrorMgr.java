package fr.index.cloud.oauth.error;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.error.ErrorDescriptor;
import org.osivia.portal.api.error.GlobalErrorHandler;
import org.osivia.portal.api.log.LogContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.index.cloud.ens.ws.WSPortalControllerContext;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;

/**
 * Error management for web services
 * 
 * @author Jean-Sébastien
 */
@Service("OAuthErrorMgr")
public class OAuthErrorMgr {


    /** Portal logger context */
    @Autowired
    private LogContext logContext;

    /** Logger. */
    private static final Log logger = LogFactory.getLog(OAuthErrorMgr.class);




    /**
     * Log an error and generate the ticket
     * 
     * @param request
     * @param e
     * @param principal
     * @return
     * @throws IOException 
     */
    public void handleError( HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {

        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response);
        
        String token = this.logContext.createContext(wsCtx, "portal", null);
        
  
        // User identifier
        String userId = null;
        if (request.getRemoteUser() != null)
            userId = request.getRemoteUser();

        // Error descriptor
        ErrorDescriptor errorDescriptor = new ErrorDescriptor(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, null, userId, null);
        errorDescriptor.setToken(token);

        // Print stack in server.log
        if (errorDescriptor.getException() != null) {
            logger.error("Technical error in web service ", errorDescriptor.getException());
        }

        // Print stack in portal_user_error.log
        GlobalErrorHandler.getInstance().logError(errorDescriptor);
        
        
        response.sendError(500);

    }


}
