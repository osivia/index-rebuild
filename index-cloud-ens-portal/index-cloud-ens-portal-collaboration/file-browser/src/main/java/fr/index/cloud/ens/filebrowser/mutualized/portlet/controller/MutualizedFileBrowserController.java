package fr.index.cloud.ens.filebrowser.mutualized.portlet.controller;

import fr.index.cloud.ens.filebrowser.commons.portlet.controller.AbstractFileBrowserController;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.model.MutualizedFileBrowserForm;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.service.MutualizedFileBrowserService;
import org.apache.commons.lang.math.NumberUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.services.workspace.filebrowser.portlet.controller.FileBrowserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;

/**
 * Mutualized file browser portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserController
 */
@Controller
@Primary
@RequestMapping("VIEW")
public class MutualizedFileBrowserController extends AbstractFileBrowserController {

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
    private MutualizedFileBrowserService service;


    /**
     * Constructor.
     */
    public MutualizedFileBrowserController() {
        super();
    }


    /**
     * Save position action mapping.
     *
     * @param request   action request
     * @param response  action response
     * @param pageIndex page index request parameter
     * @param form      form model attribute
     */
    @ActionMapping("save-position")
    public void updateScrollPosition(ActionRequest request, ActionResponse response, @RequestParam("pageIndex") String pageIndex, @ModelAttribute("form") MutualizedFileBrowserForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.savePosition(portalControllerContext, form, NumberUtils.toInt(pageIndex));
    }


    /**
     * Load page resource mapping.
     *
     * @param request   resource request
     * @param response  resource response
     * @param pageIndex page index request parameter
     */
    @ResourceMapping("load-page")
    public void loadPage(ResourceRequest request, ResourceResponse response, @RequestParam("pageIndex") String pageIndex) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Content type
        response.setContentType("text/html");

        this.service.loadPage(portalControllerContext, NumberUtils.toInt(pageIndex));
    }

}
