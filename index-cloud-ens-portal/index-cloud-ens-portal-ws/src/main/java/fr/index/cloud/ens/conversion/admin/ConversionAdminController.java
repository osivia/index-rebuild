package fr.index.cloud.ens.conversion.admin;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping("VIEW")
@SessionAttributes("conversionForm")
public class ConversionAdminController {

    /** Portlet context. */
    @Autowired
    private PortletContext portletContext;
    
    /** Form validator */
    @Autowired
    private ConversionAdminFormValidator conversionFormValidator;
    
    /** Application card Service */
    @Autowired
    private ConversionAdminService service;
    
  
    /** Internationalization bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;
    
    public ConversionAdminController() {
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
        return "view";
    }




    /**
     * Upload file action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "save", params = "upload-file")
    public void uploadFile(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.uploadFile(portalControllerContext, form);
    }


    /**
     * Delete file action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "delete-file")
    public void deleteFile(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        this.service.deleteFile(portalControllerContext, form);
    }


    /**
     * Delete patch action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "save", params = "cancel-patch")
    public void deletePatch(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        this.service.cancelPatch(portalControllerContext, form);
    }

    
    
    /**
     * Cencel file action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "save", params = "cancel-file")
    public void cancelFile(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        this.service.cancelFile(portalControllerContext, form);
    }

    
    /**
     * Delete avatar action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "save", params = "save-file")
    public void saveFile(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.saveFile(portalControllerContext, form);

    }

    

    /**
     * Upload patch action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "save", params = "upload-patch")
    public void uploadPatch(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.uploadPatch(portalControllerContext, form);
    }

    
    /**
     * Delete patch action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "save", params = "apply-patch")
    public void applyPatch(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.applyPatch(portalControllerContext, form);

    }

    
    
    /**
     * Delete patch action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form person edition form model attribute
     * @throws PortletException
     * @throws IOException
     */
    @ActionMapping(name = "save", params = "refresh-logs")
    public void refreshLogs(ActionRequest request, ActionResponse response, @ModelAttribute("conversionForm") ConversionAdminForm form, SessionStatus session)
            throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.refreshLogs(portalControllerContext, form);

    }   

    /**
     * Get person edition form model attribute.
     * 
     * @param request portlet request
     * @param response portlet response
     * @return edition form
     * @throws PortletException
     */
    @ModelAttribute("conversionForm")
    public ConversionAdminForm getConversionForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        ConversionAdminForm cardForm = service.getForm(portalControllerContext);
        return cardForm;
    }

    

    /**
     * Application edition form init binder.
     *
     * @param binder web data binder
     */
    @InitBinder("conversionForm")
    public void editionFormInitBinder(PortletRequestDataBinder binder) {
        binder.addValidators(this.conversionFormValidator);
    }

}
