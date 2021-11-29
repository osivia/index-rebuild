package fr.index.cloud.ens.application.card;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Application card portlet controller.
 * 
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping("VIEW")
public class ApplicationCardViewController {

    /** Portlet context. */
    @Autowired
    private PortletContext portletContext;
    
    /** Application card Service */
    @Autowired
    private ApplicationCardService service;
    
    /**
     * Constructor.
     */
    public ApplicationCardViewController() {
        super();
    }
    

    /**
     * View render mapping.
     *
     * @param request render request
     * @param response render response
     * @param options portlet options model attribute
     * @return view path
     * @throws PortletException
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortletException {
        
        if("deleted".equals(request.getParameter("view")))
            return "deleted";
        
        return "view";
    }
    



    /**
     * Get application card model attribute.
     * 
     * @param request portlet request
     * @param response portlet response
     * @return application card
     * @throws PortletException
     */
    @ModelAttribute("card")
    public ApplicationCard getApplicationCard(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        return this.service.getApplicationCard(portalControllerContext);
    }

    
    
    /**
     * Get card options.
     * 
     * @param request portlet request
     * @param response portlet response
     * @return options
     * @throws PortletException
     */
    @ModelAttribute("options")
    public ApplicationCardOptions getOptions(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getOptions(portalControllerContext);
    }
    
    
    /**
     * Delete application action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param options portlet option model attribute
     * @throws PortletException
     */
    @ActionMapping("delete")
    public void delete(ActionRequest request, ActionResponse response, @ModelAttribute("options") ApplicationCardOptions options) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.deleteApplication(portalControllerContext, options);
        
        response.setRenderParameter("view","deleted");
    }
    
}
