package fr.index.cloud.ens.search.portlet.controller;

import fr.index.cloud.ens.search.common.portlet.controller.SearchCommonController;
import fr.index.cloud.ens.search.portlet.model.SearchForm;
import fr.index.cloud.ens.search.portlet.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.*;
import java.io.IOException;

/**
 * Search portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonController
 */
@Controller
@RequestMapping("VIEW")
public class SearchController extends SearchCommonController {

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
    private SearchService service;


    /**
     * View render mapping.
     *
     * @param request  render request
     * @param response render response
     * @return view path
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response, @ModelAttribute("form") SearchForm form) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (StringUtils.isEmpty(form.getFolderName())) {
            // Empty response indicator
            request.setAttribute("osivia.emptyResponse", "1");

        }

        return this.service.getViewPath(portalControllerContext);
    }


    /**
     * Search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param form     search form model attribute
     */
    @ActionMapping("search")
    public void search(ActionRequest request, ActionResponse response, @ModelAttribute("form") SearchForm form) throws PortletException, IOException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Search redirection URL
        String url = this.service.getSearchRedirectionUrl(portalControllerContext, form);
        if (StringUtils.isNotEmpty(url)) {
            response.sendRedirect(url);
        }
    }
    
    
    /**
     * Search action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param form     search form model attribute
     */
    @ActionMapping("search-new-filter")
    public void searchNewFilter(ActionRequest request, ActionResponse response, @ModelAttribute("form") SearchForm form) throws PortletException, IOException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Search redirection URL
        String url = this.service.getSearchRedirectionUrl(portalControllerContext, form);
        if (StringUtils.isNotEmpty(url)) {
            response.sendRedirect(url+"#new-filter");
        }
    }


    /**
     * Reset search action mapping.
     *
     * @param request  action request
     * @param response action response
     */
    @ActionMapping("reset")
    public void reset(ActionRequest request, ActionResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.reset(portalControllerContext);
    }


    /**
     * Search filters action mapping.
     *
     * @param request  action request
     * @param response action response
     */
    @ActionMapping("search-filters")
    public void searchFilters(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        this.search(request, response, null);
    }


    /**
     * Get search form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public SearchForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }


    /**
     * Form init binder.
     *
     * @param binder data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.setDisallowedFields("view");
    }


    /**
     * Get search options URL.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return URL
     */
    @ModelAttribute("optionsUrl")
    public String getOptionsUrl(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal Controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getOptionsUrl(portalControllerContext);
    }

}
