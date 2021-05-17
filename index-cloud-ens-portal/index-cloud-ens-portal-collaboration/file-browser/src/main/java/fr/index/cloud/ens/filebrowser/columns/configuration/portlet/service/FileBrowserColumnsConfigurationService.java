package fr.index.cloud.ens.filebrowser.columns.configuration.portlet.service;

import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.FileBrowserColumnsConfigurationForm;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * File browser columns configuration portlet service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface FileBrowserColumnsConfigurationService {

    /**
     * Portlet instance.
     */
    String PORTLET_INSTANCE = "index-cloud-ens-file-browser-columns-configuration-instance";

    /**
     * File browser identifier window property.
     */
    String FILE_BROWSER_ID_WINDOW_PROPERTY = "osivia.file-browser.id";


    /**
     * Get form.
     *
     * @param portalControllerContext portal controller context
     * @return form
     */
    FileBrowserColumnsConfigurationForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Save.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     */
    void save(PortalControllerContext portalControllerContext, FileBrowserColumnsConfigurationForm form) throws PortletException;


    /**
     * Get redirection URL.
     *
     * @param portalControllerContext portal controller context
     * @return URL
     */
    String getRedirectionUrl(PortalControllerContext portalControllerContext) throws PortletException;

}
