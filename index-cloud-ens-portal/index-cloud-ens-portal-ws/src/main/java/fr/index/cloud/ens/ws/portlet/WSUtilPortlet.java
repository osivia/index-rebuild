package fr.index.cloud.ens.ws.portlet;

import java.util.Enumeration;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.osivia.portal.api.portlet.AnnotationPortletApplicationContext;
import org.osivia.portal.api.portlet.PortletAppUtils;
import org.springframework.web.context.WebApplicationContext;

import fr.index.cloud.ens.ext.etb.EtablissementService;
import fr.index.cloud.ens.ws.DriveRestController;
import fr.index.cloud.ens.ws.UserRestController;
import fr.index.cloud.ens.ws.nuxeo.NuxeoDrive;
import fr.index.cloud.oauth.config.SecurityFilter;
import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;

/**
 * Bootstrap entry point
 * 
 * @author Jean-Sébastien
 */
public class WSUtilPortlet extends CMSPortlet {
    
    
    public static ServletContext servletContext = null;

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // Portlet context
        PortletContext portletContext = getPortletContext();

        DriveRestController.portletContext = portletContext;
        NuxeoDrive.portletContext = portletContext;
        UserRestController.portletContext = portletContext;
        EtablissementService.portletContext = portletContext;
        
        //Servlet + portlets cohabitations ....
        PortletAppUtils.refreshServletApplicationContext(servletContext);
  }
    
    
    
    @Override
    public void destroy() {
        //Servlet + portlets cohabitations ...        
        PortletAppUtils.removeServletApplicationContext(servletContext);
     }

    
    
}
