package fr.index.cloud.ens.application.management;

import java.util.List;

import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Dashboard form.
 * 
 * @author JS Steux
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
@Refreshable
public class ApplicationManagementForm {


    /** The applications. */
    private List<Document> applications;
    
    /** The selected application id. */
    private String selectedApplicationId;

    /** Search filter. */
    private String filter;
    
    /** Max Results. */
    private boolean maxResults = false;
    
    
    
    /**
     * Getter for maxResults.
     * @return the maxResults
     */
    public boolean isMaxResults() {
        return maxResults;
    }


    
    /**
     * Setter for maxResults.
     * @param maxResults the maxResults to set
     */
    public void setMaxResults(boolean maxResults) {
        this.maxResults = maxResults;
    }




    /**
     * Getter for filter.
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }



    
    /**
     * Setter for filter.
     * @param filter the filter to set
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }



    /**
     * Getter for selectedApplicationId.
     * @return the selectedApplicationId
     */
    public String getSelectedApplicationId() {
        return selectedApplicationId;
    }


    
    /**
     * Setter for selectedApplicationId.
     * @param selectedApplicationId the selectedApplicationId to set
     */
    public void setSelectedApplicationId(String selectedApplicationId) {
        this.selectedApplicationId = selectedApplicationId;
    }


    /**
     * Getter for applications.
     * @return the applications
     */
    public List<Document> getApplications() {
        return applications;
    }

    
    /**
     * Setter for applications.
     * @param applications the applications to set
     */
    public void setApplications(List<Document> applications) {
        this.applications = applications;
    }
}
