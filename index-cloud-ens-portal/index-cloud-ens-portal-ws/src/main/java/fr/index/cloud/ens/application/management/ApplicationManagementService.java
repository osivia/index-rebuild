package fr.index.cloud.ens.application.management;

import org.dom4j.Element;
import org.osivia.portal.api.context.PortalControllerContext;

import fr.index.cloud.ens.dashboard.Dashboard;

import javax.portlet.PortletException;
import java.io.IOException;
import java.util.List;

/**
 * Application management service interface.
 *
 * @author JS Steux
 */
public interface ApplicationManagementService {
    
    /** Region property. */
    String REGION_PROPERTY = "template.auxiliary.region";

    /**
     * Get Application form.
     *
     * @param portalControllerContext portal controller context
     * @return dashboard form
     * @throws PortletException
     */
    ApplicationManagementForm getApplicationForm(PortalControllerContext portalControllerContext) throws PortletException;

    

    /**
     * Search applications
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void search(PortalControllerContext portalControllerContext, ApplicationManagementForm form) throws PortletException;
 
    
    
    /**
     * Search applications
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void search(PortalControllerContext portalControllerContext, ApplicationManagementForm form, String filters) throws PortletException;
    
    
    /**
     * Select application
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void select(PortalControllerContext portalControllerContext, ApplicationManagementForm form) throws PortletException;
    
    /**
     * Resolve view path.
     * 
     * @param portalControllerContext portal controller context
     * @param name view name
     * @return view path
     * @throws PortletException
     */
    String resolveViewPath(PortalControllerContext portalControllerContext, String name) throws PortletException;


}
