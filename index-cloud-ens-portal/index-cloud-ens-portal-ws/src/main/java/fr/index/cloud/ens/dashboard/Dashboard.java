package fr.index.cloud.ens.dashboard;

import java.util.List;

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
public class Dashboard {

    /** Trashed documents. */
    private List<DashboardApplication> applications;



    /**
     * Constructor.
     */
    public Dashboard() {
        super();
    }



    /**
     * Gets the applications.
     *
     * @return the applications
     */
    public List<DashboardApplication> getApplications() {
        return applications;
    }

    /**
     * Sets the applications.
     *
     * @param applications the new applications
     */
    public void setApplications(List<DashboardApplication> applications) {
        this.applications = applications;
    }



}
