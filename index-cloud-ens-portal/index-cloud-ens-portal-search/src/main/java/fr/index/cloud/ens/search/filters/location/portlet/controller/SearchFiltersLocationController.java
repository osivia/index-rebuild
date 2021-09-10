package fr.index.cloud.ens.search.filters.location.portlet.controller;

import fr.index.cloud.ens.search.common.portlet.controller.SearchCommonController;
import fr.index.cloud.ens.search.filters.location.portlet.model.SearchFiltersLocationForm;
import fr.index.cloud.ens.search.filters.location.portlet.service.SearchFiltersLocationService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Search filters location portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonController
 */
@Controller
@RequestMapping("VIEW")
public class SearchFiltersLocationController extends SearchCommonController {

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
    private SearchFiltersLocationService service;


    /**
     * Constructor.
     */
    public SearchFiltersLocationController() {
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
     * Select location action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param form     search filters location form model attribute
     */
    @ActionMapping("select")
    public void select(ActionRequest request, ActionResponse response, @ModelAttribute("form") SearchFiltersLocationForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.select(portalControllerContext, form);
    }


    /**
     * Browse resource mapping.
     *
     * @param request  resource request
     * @param response resource response
     */
    @ResourceMapping("browse")
    public void browse(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Browse
        String data = this.service.browse(portalControllerContext);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(data);
        printWriter.close();
    }


    /**
     * Get search filters location form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public SearchFiltersLocationForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }

}
