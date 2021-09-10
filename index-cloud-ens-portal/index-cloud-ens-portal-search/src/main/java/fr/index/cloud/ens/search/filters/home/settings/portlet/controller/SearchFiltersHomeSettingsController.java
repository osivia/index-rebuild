package fr.index.cloud.ens.search.filters.home.settings.portlet.controller;

import fr.index.cloud.ens.search.common.portlet.controller.SearchCommonController;
import fr.index.cloud.ens.search.filters.home.settings.portlet.model.SearchFiltersHomeSettingsForm;
import fr.index.cloud.ens.search.filters.home.settings.portlet.model.converter.SavedSearchPropertyEditor;
import fr.index.cloud.ens.search.filters.home.settings.portlet.service.SearchFiltersHomeSettingsService;
import net.sf.json.JSONArray;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Search filters home settings portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonController
 */
@Controller
@RequestMapping("VIEW")
public class SearchFiltersHomeSettingsController extends SearchCommonController {

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
    private SearchFiltersHomeSettingsService service;

    /**
     * User saved search property editor.
     */
    @Autowired
    private SavedSearchPropertyEditor savedSearchPropertyEditor;


    /**
     * Constructor.
     */
    public SearchFiltersHomeSettingsController() {
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
    public void save(ActionRequest request, ActionResponse response, @ModelAttribute("form") SearchFiltersHomeSettingsForm form) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.save(portalControllerContext, form);
    }


    /**
     * Get form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public SearchFiltersHomeSettingsForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }


    /**
     * Form init binder.
     *
     * @param binder portlet request data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.registerCustomEditor(UserSavedSearch.class, this.savedSearchPropertyEditor);
    }


    /**
     * Load user saved searches select2 resource mapping.
     *
     * @param request  resource request
     * @param response resource response
     */
    @ResourceMapping("load-saved-searches")
    public void loadSavedSearches(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Select2 results
        JSONArray results = this.service.loadSavedSearches(portalControllerContext);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(results.toString());
        printWriter.close();
    }

}
