/**
 *
 */
package fr.index.cloud.ens.directory.person.creation.portlet.controller;

import fr.index.cloud.ens.directory.person.creation.portlet.model.PersonCreationForm;
import fr.index.cloud.ens.directory.person.creation.portlet.model.PersonCreationInvalidTokenException;
import fr.index.cloud.ens.directory.person.creation.portlet.model.validation.PersonCreationFormValidator;
import fr.index.cloud.ens.directory.person.creation.portlet.service.PersonCreationService;
import fr.index.cloud.ens.directory.person.renew.portlet.controller.RenewPasswordForm;
import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.tokens.ITokenService;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.annotation.PostConstruct;
import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author Loïc Billon
 *
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class PersonCreationController extends CMSPortlet {

    /**
     * Search view window property.
     */
    public static String VIEW_WINDOW_PROPERTY = "creation.person.step";

    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;

    /** Portlet context. */
    @Autowired
    private PortletContext portletContext;

    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;

    @Autowired
    private PersonCreationService service;
    
    public static String MODEL_RELOADED = "creation.person.reload";

    @RenderMapping
    public String view(RenderRequest request, RenderResponse response, ModelMap model) throws PersonCreationInvalidTokenException {

        // Get logger person
        Person person = (Person) request.getAttribute(Constants.ATTR_LOGGED_PERSON_2);
        if (person != null) {
            return "view-logged";
        } else {
            // create new form in case of initialization
            PersonCreationForm form = (PersonCreationForm) model.get("form");
            if (form != null) {
                PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
                HttpServletRequest servletRequest = portalControllerContext.getHttpServletRequest();
                if (servletRequest != null) {
                    if (servletRequest.getParameter("init") != null) {
                      reloadForm(portalControllerContext, request, form);
                    }
                }
            }


            PersonCreationForm.CreationStep currentStep = getCurrentStep(request, response);
            return "view-" + currentStep.name().toLowerCase();
        }

    }

    @ExceptionHandler(PersonCreationInvalidTokenException.class)
    public String viewError() {
        return "view-invalid-token";
    }

    /**
     * @return
     */
    private PersonCreationForm.CreationStep getCurrentStep(PortletRequest request, PortletResponse response) {

        // Window
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());

        String view = request.getParameter(VIEW_WINDOW_PROPERTY);
        if (StringUtils.isEmpty(view)) {
            view = window.getProperty(VIEW_WINDOW_PROPERTY);
        }
        PersonCreationForm.CreationStep step;
        if (view != null) {
            step = PersonCreationForm.CreationStep.valueOf(view);
        } else {
            step = PersonCreationForm.DEFAULT;
        }

        return step;

    }

    @ModelAttribute("form")
    public PersonCreationForm getForm(PortletRequest request, PortletResponse response) throws PersonCreationInvalidTokenException, PortletException {

        PortalControllerContext portalControllerContext = new PortalControllerContext(getPortletContext(), request, response);

        // Search form
        PersonCreationForm form = this.applicationContext.getBean(PersonCreationForm.class);

        PersonCreationForm.CreationStep currentStep = getCurrentStep(request, response);

        if (currentStep != PersonCreationForm.CreationStep.CONFIRM) {
            reloadForm(portalControllerContext,request, form);
        } else {
            // If the user is logged, he will be redirected
            // But form is evaluated, so don't call proceed otherwise the procedure is lost
            Person person = (Person) request.getAttribute(Constants.ATTR_LOGGED_PERSON_2);
            if (person == null) {
                 service.proceedRegistration(portalControllerContext);
            }
        }

        return form;
    }

    private void reloadForm(PortalControllerContext portalControllerContext, PortletRequest request, PersonCreationForm form) throws PersonCreationInvalidTokenException {
        HttpServletRequest servletRequest = portalControllerContext.getHttpServletRequest();


        if (servletRequest != null) {
            if (!BooleanUtils.isTrue((Boolean) request.getAttribute(MODEL_RELOADED))) {
                form.setAcceptTermsOfService(Boolean.FALSE);
                form.setConfirmpassword("");
                form.setFirstname("");
                form.setLastname("");
                form.setMail("");
                form.setNewpassword("");
                form.setNickname("");

                String token = servletRequest.getParameter("token");

                ITokenService tokenService = Locator.findMBean(ITokenService.class, ITokenService.MBEAN_NAME);
                Map<String, String> validateToken = tokenService.validateToken(token, false);

                if (validateToken != null) {
                    form.setFirstname(validateToken.get("firstname"));
                    form.setLastname(validateToken.get("lastname"));
                    form.setMail(validateToken.get("mail"));
                } else {
                    throw new PersonCreationInvalidTokenException();

                }
                request.setAttribute(MODEL_RELOADED, true);
            }
        }
    }


    @ActionMapping(name = "submitForm")
    public void submitForm(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") PersonCreationForm form, BindingResult result,
                           SessionStatus session) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (!result.hasErrors()) {
            this.service.proceedInit(portalControllerContext, form);

            response.setRenderParameter(VIEW_WINDOW_PROPERTY, RenewPasswordForm.RenewPasswordStep.SEND.name());

        }
    }


    /**
     * Password rules information resource mapping.
     *
     * @param request  resource request
     * @param response resource response
     * @param password password request parameter
     */
    @ResourceMapping("password-information")
    public void passwordInformation(ResourceRequest request, ResourceResponse response, @RequestParam(name = "password", required = false) String password) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Password rules information
        Element information = this.service.getPasswordRulesInformation(portalControllerContext, password);

        // Content type
        response.setContentType("text/html");

        // Content
        HTMLWriter htmlWriter = new HTMLWriter(response.getPortletOutputStream());
        htmlWriter.write(information);
        htmlWriter.close();
    }


    /**
     * Person edition form init binder.
     *
     * @param binder web data binder
     */
    @InitBinder("form")
    public void initBinder(PortletRequest request, PortletResponse response, PortletRequestDataBinder binder) {

        PersonCreationForm.CreationStep step = getCurrentStep(request, response);

        if (step.equals(PersonCreationForm.CreationStep.FORM)) {
            PersonCreationFormValidator bean = applicationContext.getBean(PersonCreationFormValidator.class);
            binder.addValidators(bean);
        }

    }


    /**
     * Get terms of service URL model attribute.
     * @param request portlet request
     * @param response portlet response
     * @return URL
     */
    @ModelAttribute("termsOfServiceUrl")
    public String getTermsOfServiceUrl(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getTermsOfServiceUrl(portalControllerContext);
    }


    /**
     * Post-construct.
     *
     * @throws PortletException
     */
    @PostConstruct
    public void postConstruct() throws PortletException {
        super.init(this.portletConfig);
    }
}
