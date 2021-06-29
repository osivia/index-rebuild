package fr.index.cloud.ens.mutualization.copy.portlet.controller;

import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyConfirmationForm;
import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyForm;
import fr.index.cloud.ens.mutualization.copy.portlet.model.validation.MutualizationCopyConfirmationFormValidator;
import fr.index.cloud.ens.mutualization.copy.portlet.service.MutualizationCopyService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.*;
import java.io.IOException;
import java.util.Map;

/**
 * Mutualization copy confirmation portlet controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping(path = "VIEW", params = "step=confirmation")
public class MutualizationCopyConfirmationController {

    /**
     * Portlet context.
     */
    @Autowired
    private PortletContext portletContext;

    /**
     * Portlet service.
     */
    @Autowired
    private MutualizationCopyService service;

    /**
     * Form validator.
     */
    @Autowired
    private MutualizationCopyConfirmationFormValidator formValidator;


    /**
     * Constructor.
     */
    public MutualizationCopyConfirmationController() {
        super();
    }


    /**
     * View render mapping.
     *
     * @return view path
     */
    @RenderMapping
    public String viewConfirmation() {
        return "view-confirmation";
    }


    /**
     * Submit action mapping.
     *
     * @param request          action request
     * @param response         action response
     * @param form             form model attribute
     * @param confirmationForm confirmation form model attribute
     * @param bindingResult    binding result
     */
    @ActionMapping("submit")
    public void submit(ActionRequest request, ActionResponse response, @ModelAttribute("form") MutualizationCopyForm form, @Validated @ModelAttribute("confirmationForm") MutualizationCopyConfirmationForm confirmationForm, BindingResult bindingResult) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (bindingResult.hasErrors()) {
            // Copy render parameters
            response.setRenderParameter("step", "confirmation");
        } else {
            // Copy
            this.service.copy(portalControllerContext, form, confirmationForm);

            // Redirection
            String redirectionUrl = this.service.getRedirectionUrl(portalControllerContext, form);
            response.sendRedirect(redirectionUrl);
        }
    }


    /**
     * Get form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public MutualizationCopyForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }


    /**
     * Get confirmation form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return confirmation form
     */
    @ModelAttribute("confirmationForm")
    public MutualizationCopyConfirmationForm getConfirmationForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getConfirmationForm(portalControllerContext);
    }


    /**
     * Confirmation form init binder.
     *
     * @param dataBinder portlet request data binder
     */
    @InitBinder("confirmationForm")
    public void confirmationFormInitBinder(PortletRequestDataBinder dataBinder) {
        dataBinder.setDisallowedFields("location");
        dataBinder.setValidator(this.formValidator);
    }


    /**
     * Get copy confirmation choices model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return choices
     */
    @ModelAttribute("choices")
    public Map<String, String> getChoices(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getConfirmationChoices(portalControllerContext);
    }

}
