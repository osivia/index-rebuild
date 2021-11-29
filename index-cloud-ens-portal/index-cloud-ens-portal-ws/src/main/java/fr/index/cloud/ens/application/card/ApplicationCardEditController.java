package fr.index.cloud.ens.application.card;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

import net.sf.json.JSONObject;

@Controller
@RequestMapping(path = "VIEW", params = "view=edit")
@SessionAttributes("editionForm")
public class ApplicationCardEditController {

    /** Portlet context. */
    @Autowired
    private PortletContext portletContext;
    
    /** Form validator */
    @Autowired
    private ApplicationCardEditionFormValidator editionFormValidator;
    
    /** Application card Service */
    @Autowired
    private ApplicationCardService service;
    
  
    /** Internationalization bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;
    
    public ApplicationCardEditController() {
        super();
    }
    
    /**
     * View render mapping.
     *
     * @param request render request
     * @param response render response
     * @return view path
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
        response.setTitle(bundle.getString("APPLICATION_CARD_EDITION_TITLE"));
        return "edit";
    }
    
    /**
     * Save action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param options portlet options model attribute
     * @param form application edition form model attribute
     * @param result binding result
     * @param session session status
     * @throws PortletException
     */
    @ActionMapping(name = "save", params="save")
    public void save(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("editionForm") ApplicationCardForm form, BindingResult result, SessionStatus session) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (result.hasErrors()) {
            //Stay on the edit page
            response.setRenderParameter("view","edit");
        } else {
            this.service.saveApplication(portalControllerContext, form);
            // Complete session
            session.setComplete();
        }
    }


    /**
     * Cancel action mapping.
     * 
     * @param request portlet request
     * @param response portlet response
     * @param session session status
     */
    @ActionMapping("cancel")
    public void cancel(ActionRequest request, ActionResponse response, SessionStatus session) {
        // Complete session
        session.setComplete();
    }
    
  


    /**
     * Get application edition form model attribute.
     * 
     * @param request portlet request
     * @param response portlet response
     * @return edition form
     * @throws PortletException
     */
    @ModelAttribute("editionForm")
    public ApplicationCardForm getEditionForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        ApplicationCardForm cardForm = new ApplicationCardForm(service.getApplicationCard(portalControllerContext));
        return cardForm;
    }


    /**
     * Application edition form init binder.
     *
     * @param binder web data binder
     */
    @InitBinder("editionForm")
    public void editionFormInitBinder(PortletRequestDataBinder binder) {
        binder.addValidators(this.editionFormValidator);
    }

}
