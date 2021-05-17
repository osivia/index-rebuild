package fr.index.cloud.ens.filebrowser.portlet.service;

import fr.index.cloud.ens.filebrowser.commons.portlet.service.AbstractFileBrowserService;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserForm;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * File browser customized portlet service interface.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserService
 */
public interface CustomizedFileBrowserService extends AbstractFileBrowserService {

    /**
     * File browser identifier.
     */
    String FILE_BROWSER_ID = "default";


    @Override
    CustomizedFileBrowserForm getForm(PortalControllerContext portalControllerContext) throws PortletException;

}
