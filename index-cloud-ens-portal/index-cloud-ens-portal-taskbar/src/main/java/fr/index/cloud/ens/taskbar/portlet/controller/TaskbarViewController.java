package fr.index.cloud.ens.taskbar.portlet.controller;

import fr.index.cloud.ens.taskbar.portlet.model.Taskbar;
import fr.index.cloud.ens.taskbar.portlet.model.TaskbarSearchForm;
import fr.index.cloud.ens.taskbar.portlet.service.TaskbarService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Taskbar portlet view controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping("VIEW")
public class TaskbarViewController {

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
    private TaskbarService service;


    /**
     * Constructor.
     */
    public TaskbarViewController() {
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
    public String view(RenderRequest request, RenderResponse response) {
        return "view";
    }


    /**
     * Drop action mapping.
     *
     * @param request   action request
     * @param response  action response
     * @param sourceIds source identifiers request parameter
     * @param targetId  target identifier request parameter
     */
    @ActionMapping("drop")
    public void drop(ActionRequest request, ActionResponse response, @RequestParam("sourceIds") String sourceIds, @RequestParam("targetId") String targetId)
            throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        this.service.drop(portalControllerContext, Arrays.asList(StringUtils.split(sourceIds, ",")), targetId);
    }


    /**
     * Reset search filters action mapping.
     *
     * @param request    action request
     * @param response   action response
     * @param searchForm search form model attribute
     */
    @ActionMapping("reset")
    public void resetSearchFilters(ActionRequest request, ActionResponse response, @ModelAttribute("searchForm") TaskbarSearchForm searchForm) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        this.service.resetSearchFilters(portalControllerContext, searchForm);
    }


    /**
     * Search action mapping.
     *
     * @param request    action request
     * @param response   action response
     * @param searchForm search form model attribute
     */
    @ActionMapping("search")
    public void search(ActionRequest request, ActionResponse response, @ModelAttribute("searchForm") TaskbarSearchForm searchForm) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.search(portalControllerContext, searchForm);
    }


    /**
     * Advanced search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param titleKey title internationalization key request parameter
     */
    @ActionMapping("advanced-search")
    public void advancedSearch(ActionRequest request, ActionResponse response, @RequestParam(name = "titleKey", defaultValue = "TASKBAR_ADVANCED_SEARCH") String titleKey) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Redirection
        String url = this.service.getAdvancedSearchUrl(portalControllerContext, titleKey);
        response.sendRedirect(url);
    }

    
    /**
     * Advanced search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param titleKey title internationalization key request parameter
     */
    @ActionMapping("advanced-search-new-filter")
    public void advancedSearchNewFilter(ActionRequest request, ActionResponse response, @RequestParam(name = "titleKey", defaultValue = "TASKBAR_ADVANCED_SEARCH") String titleKey) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Redirection
        String url = this.service.getAdvancedSearchUrl(portalControllerContext, titleKey);
        response.sendRedirect(url+"#new-filter");
    }


    /**
     * Saved search action mapping.
     *
     * @param request    action request
     * @param response   action response
     * @param id         saved search identifier request parameter
     * @param searchForm search form model attribute
     */
    @ActionMapping("saved-search")
    public void savedSearch(ActionRequest request, ActionResponse response, @RequestParam("id") String id, @ModelAttribute("searchForm") TaskbarSearchForm searchForm) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Redirection
        String url = this.service.getSavedSearchUrl(portalControllerContext, searchForm, NumberUtils.toInt(id));
        response.sendRedirect(url);
    }


    /**
     * Delete saved search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param id       saved search identifier request parameter
     */
    @ActionMapping("delete-saved-search")
    public void deleteSavedSearch(ActionRequest request, ActionResponse response, @RequestParam("id") String id) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.deleteSavedSearch(portalControllerContext, NumberUtils.toInt(id));
    }


    /**
     * Save collapse state action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param id       collapse identifier request parameter
     * @param show     collapse show indicator request parameter
     * @param taskbar  taskbar model attribute
     */
    @ActionMapping("save-collapse-state")
    public void saveCollapseState(ActionRequest request, ActionResponse response, @RequestParam("id") String id, @RequestParam("show") String show, @ModelAttribute("taskbar") Taskbar taskbar) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.saveCollapseState(portalControllerContext, taskbar, id, BooleanUtils.toBoolean(show));
    }


    /**
     * Lazy loading resource mapping.
     *
     * @param request  resource request
     * @param response resource response
     * @param path     parent folder path request parameter
     */
    @ResourceMapping("lazy-loading")
    public void lazyLoading(ResourceRequest request, ResourceResponse response, @RequestParam("path") String path) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        // JSON
        JSONArray array = this.service.getFolderChildren(portalControllerContext, path);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(array.toString());
        printWriter.close();
    }


    /**
     * Load levels select2 vocabulary resource mapping.
     *
     * @param request        resource request
     * @param response       resource response
     * @param vocabularyName vocabulary name request parameter
     * @param filter         select2 filter request parameter
     */
    @ResourceMapping("load-vocabulary")
    public void loadLevels(ResourceRequest request, ResourceResponse response, @RequestParam("vocabulary") String vocabularyName, @RequestParam(name = "filter", required = false) String filter) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        // Select2 results
        JSONArray results = this.service.loadVocabulary(portalControllerContext, vocabularyName, filter);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(results.toString());
        printWriter.close();
    }


    /**
     * Get taskbar model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return taskbar
     */
    @ModelAttribute("taskbar")
    public Taskbar getTaskbar(PortletRequest request, PortletResponse response) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.service.getTaskbar(portalControllerContext);
    }


    /**
     * Get taskbar search form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return search form
     */
    @ModelAttribute("searchForm")
    public TaskbarSearchForm getSearchForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.service.getTaskbarSearchForm(portalControllerContext);
    }

}
