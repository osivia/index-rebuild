package fr.index.cloud.ens.search.portlet.controller;

import fr.index.cloud.ens.search.portlet.model.SearchView;
import fr.index.cloud.ens.search.portlet.model.SearchWindowProperties;
import fr.index.cloud.ens.search.portlet.service.SearchService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.*;
import java.util.Arrays;
import java.util.List;

/**
 * Search portlet administration controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping("ADMIN")
public class SearchAdminController {

    /**
     * Portlet context.
     */
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
     * @return view path
     */
    @RenderMapping
    public String view() {
        return "admin";
    }


    /**
     * Save search window properties.
     *
     * @param request          portlet request
     * @param response         portlet response
     * @param windowProperties search window properties model attribute
     */
    @ActionMapping("save")
    public void save(ActionRequest request, ActionResponse response, @ModelAttribute("windowProperties") SearchWindowProperties windowProperties) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.setWindowProperties(portalControllerContext, windowProperties);

        response.setPortletMode(PortletMode.VIEW);
        response.setWindowState(WindowState.NORMAL);
    }


    /**
     * Get search window properties model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return window properties
     */
    @ModelAttribute("windowProperties")
    public SearchWindowProperties getWindowProperties(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getWindowProperties(portalControllerContext);
    }


    /**
     * Get search views model attribute.
     *
     * @return views
     */
    @ModelAttribute("views")
    public List<SearchView> getViews() {
        return Arrays.asList(SearchView.values());
    }

}
