package fr.index.cloud.ens.search.eraser.portlet.controller;

import fr.index.cloud.ens.search.common.portlet.controller.SearchCommonController;
import fr.index.cloud.ens.search.eraser.portlet.service.SearchEraserService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;

/**
 * Search eraser portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonController
 */
@Controller
@RequestMapping("VIEW")
public class SearchEraserController extends SearchCommonController {

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
    private SearchEraserService service;


    /**
     * Constructor.
     */
    public SearchEraserController() {
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
     * Reset search action mapping.
     *
     * @param request  action request
     * @param response action response
     */
    @ActionMapping("reset")
    public void reset(ActionRequest request, ActionResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.reset(portalControllerContext);
    }

}
