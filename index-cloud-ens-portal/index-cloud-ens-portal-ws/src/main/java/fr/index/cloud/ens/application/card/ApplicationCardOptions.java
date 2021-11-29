package fr.index.cloud.ens.application.card;


import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION)
@Refreshable
public class ApplicationCardOptions {


    /** Editable application indicator. */
    private boolean editable;
    
    
    /** The app id. */
    private String appId;
    
    
    /**
     * Getter for appId.
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }


    
    /**
     * Setter for appId.
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }


    public ApplicationCardOptions() {
        super();
    }

    
    /**
     * Getter for editable.
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    
    /**
     * Setter for editable.
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

  
}
