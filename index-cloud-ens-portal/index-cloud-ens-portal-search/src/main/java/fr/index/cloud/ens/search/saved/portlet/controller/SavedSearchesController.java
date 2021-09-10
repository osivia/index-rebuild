package fr.index.cloud.ens.search.saved.portlet.controller;

import fr.index.cloud.ens.search.common.portlet.controller.SearchCommonController;
import fr.index.cloud.ens.search.saved.portlet.model.SavedSearchesForm;
import fr.index.cloud.ens.search.saved.portlet.service.SavedSearchesService;
import org.apache.commons.lang.math.NumberUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.*;
import java.io.IOException;

/**
 * Saved searches portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonController
 */
@Controller
@RequestMapping("VIEW")
public class SavedSearchesController extends SearchCommonController {

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
    private SavedSearchesService service;


    /**
     * Constructor.
     */
    public SavedSearchesController() {
        super();
    }


    /**
     * View render mapping.
     *
     * @param request  render request
     * @param response render response
     * @return view path
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.renderView(portalControllerContext);
    }


    /**
     * Search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param id       saved search identifier request parameter
     * @param form     saved searches form model attribute
     */
    @ActionMapping("search")
    public void search(ActionRequest request, ActionResponse response, @RequestParam("id") String id, @ModelAttribute("form") SavedSearchesForm form) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Redirection
        String url = this.service.getSavedSearchUrl(portalControllerContext, form, NumberUtils.toInt(id));
        response.sendRedirect(url);
    }


    /**
     * Delete action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param id       saved search identifier request parameter
     */
    @ActionMapping("delete")
    public void delete(ActionRequest request, ActionResponse response, @RequestParam("id") String id) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.deleteSavedSearch(portalControllerContext, NumberUtils.toInt(id));
    }


    /**
     * Get saved searches form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public SavedSearchesForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }

}
