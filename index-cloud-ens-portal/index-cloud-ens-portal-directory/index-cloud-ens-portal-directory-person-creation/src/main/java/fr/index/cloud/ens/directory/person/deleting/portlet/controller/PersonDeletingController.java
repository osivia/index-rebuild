package fr.index.cloud.ens.directory.person.deleting.portlet.controller;

import fr.index.cloud.ens.directory.person.deleting.portlet.model.PersonDeletingForm;
import fr.index.cloud.ens.directory.person.deleting.portlet.model.validation.PersonDeletingFormValidator;
import fr.index.cloud.ens.directory.person.deleting.portlet.service.PersonDeletingService;
import org.osivia.portal.api.context.PortalControllerContext;
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

import javax.portlet.*;
import java.io.IOException;

/**
 * Person deleting portlet controller.
 *
 * @author Loïc Billon
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping("VIEW")
@SessionAttributes("form")
public class PersonDeletingController {

    /**
     * Portlet context.
     */
    @Autowired
    private PortletContext portletContext;

    /**
     * Portlet service.
     */
    @Autowired
    private PersonDeletingService service;

    /**
     * Person deleting form validator.
     */
    @Autowired
    private PersonDeletingFormValidator formValidator;


    /**
     * Constructor.
     */
    public PersonDeletingController() {
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
    public String view(RenderRequest request, RenderResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getViewPath(portalControllerContext);
    }


    /**
     * View form action mapping.
     *
     * @param request  action request
     * @param response action response
     */
    @ActionMapping("view-form")
    public void viewForm(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Redirection URL
        String url = this.service.getViewFormUrl(portalControllerContext);

        response.sendRedirect(url);
    }


    /**
     * Cancel action mapping.
     *
     * @param request       action request
     * @param response      action response
     * @param sessionStatus session status
     */
    @ActionMapping("cancel")
    public void cancel(ActionRequest request, ActionResponse response, SessionStatus sessionStatus) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Redirection URL
        String url = this.service.getCancelUrl(portalControllerContext);

        sessionStatus.setComplete();

        response.sendRedirect(url);
    }


    /**
     * Check action mapping.
     *
     * @param request       action request
     * @param response      action response
     * @param form          person deleting form model attribute
     * @param bindingResult binding result
     */
    @ActionMapping("check")
    public void check(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") PersonDeletingForm form, BindingResult bindingResult) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (!bindingResult.hasErrors()) {
            this.service.validate(portalControllerContext, form);
        }
    }


    /**
     * Delete action mapping.
     *
     * @param request       action request
     * @param response      action response
     * @param form          person deleting form model attribute
     * @param sessionStatus session status
     */
    @ActionMapping("delete")
    public void delete(ActionRequest request, ActionResponse response, @ModelAttribute("form") PersonDeletingForm form, SessionStatus sessionStatus) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.delete(portalControllerContext, form);

        sessionStatus.setComplete();
    }


    /**
     * Get person deleting form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public PersonDeletingForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }


    /**
     * Person deleting form init binder.
     *
     * @param binder data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.addValidators(this.formValidator);
        binder.setDisallowedFields("person", "validated");
    }

}
