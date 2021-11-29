package fr.index.cloud.ens.ws;

import org.osivia.portal.core.web.IWebIdService;

/**
 * Wraps current exception
 * 
 * @author Jean-Sébastien
 */
public class GenericException extends Exception {

    private static final long serialVersionUID = -7558437064829642698L;
    
    Throwable e;
    String searchIdentifier;

    public GenericException(Throwable e, String searchIdentifier) {
        super();
        this.e = e;
        
        if( searchIdentifier.startsWith(IWebIdService.FETCH_PATH_PREFIX))    {
            this.searchIdentifier = searchIdentifier.substring(IWebIdService.FETCH_PATH_PREFIX.length());
        }   else
            this.searchIdentifier = searchIdentifier;
         
    }
    

    /**
     * Getter for contentId.
     * 
     * @return the contentId
     */
    public String getSearchIdentifier() {
        return searchIdentifier;
    }

   
    /**
     * Getter for e.
     * 
     * @return the e
     */
    public Throwable getE() {
        return e;
    }


}
