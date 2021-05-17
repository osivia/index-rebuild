package fr.index.cloud.ens.filebrowser.commons.portlet.controller;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserForm;
import fr.index.cloud.ens.filebrowser.commons.portlet.service.AbstractFileBrowserService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.services.workspace.filebrowser.portlet.controller.FileBrowserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.portlet.bind.annotation.ActionMapping;

import javax.portlet.*;

/**
 * File browser controller abstract super-class.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserController
 */
public abstract class AbstractFileBrowserController extends FileBrowserController {

    /**
     * Portlet context.
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private PortletContext portletContext;

    /**
     * Portlet service.
     */
    @Autowired
    private AbstractFileBrowserService service;


    /**
     * Constructor.
     */
    public AbstractFileBrowserController() {
        super();
    }


    /**
     * Reset search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param form     form model attribute
     */
    @ActionMapping("reset-search")
    public void resetSearch(ActionRequest request, ActionResponse response, @ModelAttribute("form") AbstractFileBrowserForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.resetSearch(portalControllerContext, form);
    }


    /**
     * Get columns configuration URL model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return URL
     */
    @ModelAttribute("columnsConfigurationUrl")
    public String getColumnsConfigurationUrl(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getColumnsConfigurationUrl(portalControllerContext);
    }

}
