package fr.index.cloud.ens.filebrowser.columns.configuration.portlet.controller;

import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.FileBrowserColumnsConfigurationForm;
import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.service.FileBrowserColumnsConfigurationService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.*;
import java.io.IOException;

/**
 * File browser columns configuration portlet controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping("VIEW")
public class FileBrowserColumnsConfigurationController {

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
    private FileBrowserColumnsConfigurationService service;


    /**
     * Constructor.
     */
    public FileBrowserColumnsConfigurationController() {
        super();
    }


    /**
     * View render mapping.
     *
     * @return view path
     */
    @RenderMapping
    public String view() {
        return "view";
    }


    /**
     * Save action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param form     form model attribute
     */
    @ActionMapping("save")
    public void save(ActionRequest request, ActionResponse response, @ModelAttribute("form") FileBrowserColumnsConfigurationForm form) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.save(portalControllerContext, form);

        // Redirection
        String redirectionUrl = this.service.getRedirectionUrl(portalControllerContext);
        response.sendRedirect(redirectionUrl);
    }


    /**
     * Get form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public FileBrowserColumnsConfigurationForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }

}
