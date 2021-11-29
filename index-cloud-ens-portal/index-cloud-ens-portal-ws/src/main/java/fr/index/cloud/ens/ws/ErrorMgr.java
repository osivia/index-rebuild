package fr.index.cloud.ens.ws;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.error.ErrorDescriptor;
import org.osivia.portal.api.error.GlobalErrorHandler;
import org.osivia.portal.api.log.LogContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;

/**
 * Error management for web services
 * 
 * @author Jean-Sébastien
 */
@Service
public class ErrorMgr {

    public static final int ERR_OK = 0;
    public static final int ERR_NOT_FOUND = 100;
    public static final int ERR_FORBIDDEN = 101;
    public static final int ERR_INTERNAL = 999;

    /** Portal logger context */
    @Autowired
    private LogContext logContext;

    /** Logger. */
    private static final Log logger = LogFactory.getLog(DriveRestController.class);


    public static String getErrorMsg(int errorCode) {

        switch (errorCode) {
            case ERR_NOT_FOUND:
                return "Content {id} not found";
            case ERR_FORBIDDEN:
                return "Content {id} forbidden";
            case ERR_INTERNAL:
                return "Server Internal Error {token}";
        }
        return null;
    }


    /**
     * Returns an application error
     * 
     * @param errorCode
     * @param errorMsg
     * @return
     */
    public Map<String, Object> getErrorResponse(int errorCode, String errorMsg) {
        Map<String, Object> response = new LinkedHashMap<>();

        addErrorResponse(response, errorCode, errorMsg);
        
        return response;
    }

    /**
     * Add an application error to en existing map
     * 
     * @param errorCode
     * @param errorMsg
     * @return
     */
    public void addErrorResponse(Map<String, Object> response, int errorCode, String errorMsg) {
        response.put("returnCode", errorCode);
        if (StringUtils.isNotEmpty(errorMsg))
            response.put("errorMessage", errorMsg);
    }

    /**
     * Handle default portal exceptions
     * 
     * @param request
     * @param e
     * @param principal
     * @return
     */
    public Map<String, Object> handleDefaultExceptions(WSPortalControllerContext ctx, Exception e) {

        String token = null;
        boolean logError = true;

        String searchIdentifier = "";

        int returnCode = ErrorMgr.ERR_INTERNAL;
        
        if( e instanceof NotFoundException) {
            logError = false;
            searchIdentifier = ((GenericException) e).getSearchIdentifier();
            returnCode = ErrorMgr.ERR_NOT_FOUND;
        }

        if (e instanceof GenericException) {
            if (((GenericException) e).getE() instanceof NuxeoException) {
                NuxeoException nxe = (NuxeoException) ((GenericException) e).getE();
                if (nxe.getErrorCode() == NuxeoException.ERROR_NOTFOUND) {
                    returnCode = ErrorMgr.ERR_NOT_FOUND;
                    logError = false;
                    if (searchIdentifier != null)
                        searchIdentifier = ((GenericException) e).getSearchIdentifier();
                } else if (nxe.getErrorCode() == NuxeoException.ERROR_FORBIDDEN) {
                    returnCode = ErrorMgr.ERR_FORBIDDEN;
                    logError = false;
                    if (searchIdentifier != null)                    
                        searchIdentifier = ((GenericException) e).getSearchIdentifier();
                }
            }
        }

        String errorMessage = ErrorMgr.getErrorMsg(returnCode);
        if( searchIdentifier != null)  {
            errorMessage = errorMessage.replaceAll("\\{id\\}", searchIdentifier);
        }

        if (logError) {
            token = logError(ctx, e);
            errorMessage = errorMessage.replaceAll("\\{token\\}", token);
        }

        Map<String, Object> response = getErrorResponse(returnCode, errorMessage);


        return response;
    }


    /**
     * Log an error and generate the ticket
     * 
     * @param request
     * @param e
     * @param principal
     * @return
     */
    public String logError(WSPortalControllerContext ctx, Exception e) {


        String token = this.logContext.createContext(ctx, "portal", null);
        


        // User identifier
        String userId = null;
        if (ctx.getPrincipal() != null)
            userId = ctx.getPrincipal().getName();

        // Error descriptor
        ErrorDescriptor errorDescriptor = new ErrorDescriptor(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, null, userId, null);
        errorDescriptor.setToken(token);

        // Print stack in server.log
        if (errorDescriptor.getException() != null) {
            logger.error("Technical error in web service ", errorDescriptor.getException());
        }

        // Print stack in portal_user_error.log
        GlobalErrorHandler.getInstance().logError(errorDescriptor);

        return token;
    }

    
    
    
    

}
