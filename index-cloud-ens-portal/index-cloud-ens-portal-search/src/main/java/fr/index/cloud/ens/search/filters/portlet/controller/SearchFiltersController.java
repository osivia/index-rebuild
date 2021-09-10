package fr.index.cloud.ens.search.filters.portlet.controller;

import fr.index.cloud.ens.search.common.portlet.controller.SearchCommonController;
import fr.index.cloud.ens.search.filters.portlet.model.CustomPerson;
import fr.index.cloud.ens.search.filters.portlet.model.SearchFiltersDateRange;
import fr.index.cloud.ens.search.filters.portlet.model.SearchFiltersForm;
import fr.index.cloud.ens.search.filters.portlet.model.SearchFiltersSizeRange;
import fr.index.cloud.ens.search.filters.portlet.model.SearchFiltersSizeUnit;
import fr.index.cloud.ens.search.filters.portlet.model.converter.PersonPropertyEditor;
import fr.index.cloud.ens.search.filters.portlet.model.converter.SearchFiltersDatePropertyEditor;
import fr.index.cloud.ens.search.filters.portlet.service.SearchFiltersService;
import net.sf.json.JSONObject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.osivia.directory.v2.model.CollabProfile;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Search filters portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonController
 */
@Controller
@RequestMapping("VIEW")
@SessionAttributes("form")
public class SearchFiltersController extends SearchCommonController {

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
    private SearchFiltersService service;

    /**
     * Search filters date property editor.
     */
    @Autowired
    private SearchFiltersDatePropertyEditor datePropertyEditor;
    
    /** Bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;
    
    /** Person property editor. */
    @Autowired
    private PersonPropertyEditor personPropertyEditor;


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
     * Search action mapping.
     *
     * @param request       action request
     * @param response      action response
     * @param form          search filters form model attribute
     * @param sessionStatus session status
     */
    @ActionMapping(name = "submit", params = "search")
    public void search(ActionRequest request, ActionResponse response, @ModelAttribute("form") SearchFiltersForm form,  SessionStatus sessionStatus) throws PortletException, IOException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Complete session
        sessionStatus.setComplete();

        // Search redirection URL
        String url = this.service.getSearchRedirectionUrl(portalControllerContext, form);

        response.sendRedirect(url);
    }


    /**
     * Update action mapping.
     *
     * @param form search filters form model attribute
     */
    @ActionMapping(name = "submit", params = "update")
    public void update(@ModelAttribute("form") SearchFiltersForm form) {
        // Do nothing: model has been updated
    }


    /**
     * Update location action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param form     search filters form model attribute
     */
    @ActionMapping(name = "submit", params = "update-location")
    public void updateLocation(ActionRequest request, ActionResponse response, @ModelAttribute("form") SearchFiltersForm form) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.updateLocation(portalControllerContext, form);
    }


    /**
     * Save search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param form     search filters form model attribute
     */
    @ActionMapping(name = "submit", params = "save-search")
    public void saveSearch(ActionRequest request, ActionResponse response, @ModelAttribute("form") SearchFiltersForm form, BindingResult result)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        if (StringUtils.isEmpty(form.getSavedSearchDisplayName())) {
            // Bundle
            Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
            
            ObjectError error = new FieldError("savedSearchDisplayName", "savedSearchDisplayName", bundle.getString("SEARCH_FILTERS_MANDATORY_FILTER"));
            result.addError(error);
        } else {
           // Redirection
            String url = this.service.saveSearch(portalControllerContext, form);
            response.sendRedirect(url);
        }
    }



    
    /**
     * Search persons resource mapping.
     *
     * @param request resource request
     * @param response resource response
     * @param options options model attribute
     * @param filter search filter request parameter
     * @param page pagination page number request parameter
     * @param tokenizer tokenizer indicator request parameter
     * @throws PortletException
     * @throws IOException
     */
    @ResourceMapping("search-member")
    public void search(ResourceRequest request, ResourceResponse response,  @RequestParam(value = "filter", required = false) String filter, @RequestParam(value = "page", required = false) String page,
            @RequestParam(value = "tokenizer", required = false) String tokenizer) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Search results
        JSONObject results = this.service.searchPersons(portalControllerContext, filter, NumberUtils.toInt(page, 1),
                BooleanUtils.toBoolean(tokenizer));

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(results.toString());
        printWriter.close();
    }



    /**
     * Get search filters form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public SearchFiltersForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }


    /**
     * Search filters form init binder.
     *
     * @param binder portlet request data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.registerCustomEditor(Date.class, this.datePropertyEditor);
        binder.registerCustomEditor(CustomPerson.class, this.personPropertyEditor);
    }


    /**
     * Get location URL model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return URL
     */
    @ModelAttribute("locationUrl")
    public String getLocationUrl(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getLocationUrl(portalControllerContext);
    }


    /**
     * Get size ranges model attribute.
     *
     * @return size ranges
     */
    @ModelAttribute("sizeRanges")
    public List<SearchFiltersSizeRange> getSizeRanges() {
        return Arrays.asList(SearchFiltersSizeRange.values());
    }


    /**
     * Get date ranges model attribute.
     *
     * @return date ranges
     */
    @ModelAttribute("dateRanges")
    public List<SearchFiltersDateRange> getDateRanges() {
        return Arrays.asList(SearchFiltersDateRange.values());
    }


    /**
     * Get size units model attribute.
     *
     * @return size units
     */
    @ModelAttribute("sizeUnits")
    public List<SearchFiltersSizeUnit> getSizeUnits() {
        return Arrays.asList(SearchFiltersSizeUnit.values());
    }

}
