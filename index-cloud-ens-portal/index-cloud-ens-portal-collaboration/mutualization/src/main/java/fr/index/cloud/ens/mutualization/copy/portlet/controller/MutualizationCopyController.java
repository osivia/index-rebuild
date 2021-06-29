package fr.index.cloud.ens.mutualization.copy.portlet.controller;

import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyForm;
import fr.index.cloud.ens.mutualization.copy.portlet.model.validation.MutualizationCopyFormValidator;
import fr.index.cloud.ens.mutualization.copy.portlet.service.MutualizationCopyService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Mutualization copy portlet controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping("VIEW")
public class MutualizationCopyController {

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
    private MutualizationCopyFormValidator formValidator;


    /**
     * Constructor.
     */
    public MutualizationCopyController() {
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
     * Submit action mapping.
     *
     * @param request       action request
     * @param response      action response
     * @param form          form model attribute
     * @param bindingResult binding result
     */
    @ActionMapping("submit")
    public void submit(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") MutualizationCopyForm form, BindingResult bindingResult) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (!bindingResult.hasErrors()) {
            // Check if document already exists
            boolean alreadyExists = this.service.alreadyExists(portalControllerContext, form);

            if (alreadyExists) {
                // Confirmation
                response.setRenderParameter("step", "confirmation");
            } else {
                // Copy
                this.service.copy(portalControllerContext, form, null);

                // Redirection
                String redirectionUrl = this.service.getRedirectionUrl(portalControllerContext, form);
                response.sendRedirect(redirectionUrl);
            }
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
     * Form init binder.
     *
     * @param dataBinder portlet request data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder dataBinder) {
        dataBinder.setDisallowedFields("documentPath", "basePath");
        dataBinder.setValidator(this.formValidator);
    }


    /**
     * Browse resource mapping.
     *
     * @param request  resource request
     * @param response resource response
     */
    @ResourceMapping("browse")
    public void browse(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Browse
        String data = this.service.browse(portalControllerContext);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(data);
        printWriter.close();
    }

}
