/**
 * 
 */
package fr.index.cloud.ens.application.management;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 * @author JS Steux
 *
 */
@Controller
@RequestMapping(value = "VIEW")
public class ApplicationManagementController {

    /** Portlet service. */
    @Autowired
    private ApplicationManagementService service;
    
    /** Portlet context. */
    @Autowired
    private PortletContext portletContext;


	@RenderMapping
	public String view() {
		return "view";
	}


	

    /**
     * Select user action mapping.
     *
     * @param request action request
     * @param response action response
     * @param form form model attribute
     * @throws PortletException
     */
    @ActionMapping(name = "select", params = "select")
    public void select(ActionRequest request, ActionResponse response, @ModelAttribute("applicationForm") ApplicationManagementForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.select(portalControllerContext, form);
    }



    
    
    /**
     * Search resource mapping.
     *
     * @param request resource request
     * @param response resource response
     * @param filter search filter request parameter
     * @param form form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ResourceMapping("search")
    public void search(ResourceRequest request, ResourceResponse response, @RequestParam(name = "filters", required = false) String filters,
            @ModelAttribute("applicationForm") ApplicationManagementForm form) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Search
        this.service.search(portalControllerContext, form, filters);

        request.setAttribute("applicationForm", form);

        // View path
        String path = this.service.resolveViewPath(portalControllerContext, "results");
        PortletRequestDispatcher dispatcher = this.portletContext.getRequestDispatcher(path);
        dispatcher.include(request, response);
    }

    /**
     * Apply filter action mapping.
     *
     * @param request action request
     * @param response action response
     * @param form form model attribute
     * @throws PortletException
     */
    @ActionMapping(name = "select", params = "apply-filter")
    public void applyFilter(ActionRequest request, ActionResponse response, @ModelAttribute("form") ApplicationManagementForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.search(portalControllerContext, form);
    }
    
    

    @ModelAttribute("applicationForm")
    public ApplicationManagementForm getApplicationForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.service.getApplicationForm(portalControllerContext);
    }



}
