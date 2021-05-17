package fr.index.cloud.ens.filebrowser.mutualized.portlet.service;

import fr.index.cloud.ens.filebrowser.commons.portlet.service.AbstractFileBrowserService;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.model.MutualizedFileBrowserForm;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.model.MutualizedFileBrowserWindowProperties;
import fr.index.cloud.ens.filebrowser.portlet.service.CustomizedFileBrowserService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserWindowProperties;

import javax.portlet.PortletException;
import java.io.IOException;

/**
 * Mutualized file browser portlet service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface MutualizedFileBrowserService extends AbstractFileBrowserService {

    /**
     * Portlet instance.
     */
    String PORTLET_INSTANCE = "index-cloud-ens-mutualized-file-browser-instance";

    /**
     * File browser identifier.
     */
    String FILE_BROWSER_ID = "mutualized";


    /**
     * Page size window property.
     */
    String PAGE_SIZE_WINDOW_PROPERTY = "osivia.file-browser.page-size";

    /**
     * Page size default value.
     */
    int PAGE_SIZE_DEFAULT_VALUE = 50;
    
    /**
     * Author selector identifier.
     */
    String AUTHORS_SELECTOR_ID = "authors";


    /**
     * Get form.
     *
     * @param portalControllerContext portal controller context
     * @return form
     */
    MutualizedFileBrowserForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Save position.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     * @param pageIndex               page index
     */
    void savePosition(PortalControllerContext portalControllerContext, MutualizedFileBrowserForm form, int pageIndex) throws PortletException;


    /**
     * Load page.
     *
     * @param portalControllerContext portal controller context
     * @param pageIndex               page index
     */
    void loadPage(PortalControllerContext portalControllerContext, int pageIndex) throws PortletException, IOException;

}
