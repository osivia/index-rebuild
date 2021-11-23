/**
 * 
 */
package fr.index.cloud.ens.directory.person.renew.portlet.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

import fr.index.cloud.ens.directory.person.renew.portlet.controller.RenewPasswordForm.RenewPasswordStep;
import fr.index.cloud.ens.directory.person.renew.portlet.service.RenewPasswordService;
import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;

/**
 * @author Loïc Billon
 *
 */
@Controller
@RequestMapping(value = "VIEW")
@SessionAttributes("form")
public class RenewPasswordPortletController extends CMSPortlet {

    /**
     * Search view window property.
     */
    public static String VIEW_WINDOW_PROPERTY = "renew.password.step";
	
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
    private RenewPasswordService service;
	
	@RenderMapping
	public String view(RenderRequest request, RenderResponse response) {
		
		// Get logger person
        Person person = (Person) request.getAttribute(Constants.ATTR_LOGGED_PERSON_2);
        if(person != null) {
        	return "view-logged";
        }
        else {
        	return "view-" + getCurrentStep(request, response).name().toLowerCase();
        }
		
	}
	
	/**
	 * @return
	 */
	private RenewPasswordStep getCurrentStep(PortletRequest request, PortletResponse response) {
		
        // Window
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        
        String view = request.getParameter(VIEW_WINDOW_PROPERTY);
        if(StringUtils.isEmpty(view)) {
        	view = window.getProperty(VIEW_WINDOW_PROPERTY);
        }
        RenewPasswordStep step;
        if(view != null) {
        	step = RenewPasswordStep.valueOf(view);
        }
        else {
        	step = RenewPasswordForm.DEFAULT;
        }
    	
        return step;

	}

	@ModelAttribute("form")
	public RenewPasswordForm getForm(PortletRequest request, PortletResponse response) {
		// Search form
		RenewPasswordForm form = this.applicationContext.getBean(RenewPasswordForm.class);
		
        // Window
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());
        
        String mail = window.getProperty("mail");
        if(mail != null) {
        	form.setMail(mail);
        }

		return form;
	}
	
	@ActionMapping(name = "sendMail")
	public void sendMail(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") RenewPasswordForm form, BindingResult result,
			SessionStatus session) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (result.hasErrors()) {
        	copyRenderParameters(request, response);
        } else {
        	this.service.proceedInit(portalControllerContext, form);

        	response.setRenderParameter(VIEW_WINDOW_PROPERTY, RenewPasswordStep.SEND.name());
        	
        }
	}

	private void copyRenderParameters(ActionRequest request, ActionResponse response) {
		// Copy render parameters
		Map<String, String[]> parameters = request.getPrivateParameterMap();
		if (MapUtils.isNotEmpty(parameters)) {
		    for (Entry<String, String[]> entry : parameters.entrySet()) {
		        response.setRenderParameter(entry.getKey(), entry.getValue());
		    }
		}
	}
	
	
	@ActionMapping(name = "submitPassword")
	public void submitPassword(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") RenewPasswordForm form, BindingResult result,
			SessionStatus session) throws PortletException {
		
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (result.hasErrors()) {
            copyRenderParameters(request, response);
        } else {
        	service.proceedPassword(portalControllerContext, form); 
        	
        	response.setRenderParameter(VIEW_WINDOW_PROPERTY, RenewPasswordStep.CONFIRM.name());
        }
		
	}
	

    /**
     * Password rules information resource mapping.
     *
     * @param request  resource request
     * @param response resource response
     * @param password password
     */
    @ResourceMapping("password-information")
    public void passwordInformation(ResourceRequest request, ResourceResponse response, @RequestParam(name = "password", required = false) String newpassword) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Password rules information
        Element information = this.service.getPasswordRulesInformation(portalControllerContext, newpassword);

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

    	RenewPasswordStep step = getCurrentStep(request, response);

    	if(step.equals(RenewPasswordStep.INIT)) {
    		RenewPasswordInitFormValidator bean = applicationContext.getBean(RenewPasswordInitFormValidator.class);
    		binder.addValidators(bean);
    	}
    	else if (step.equals(RenewPasswordStep.PASSWORD)) {
    		RenewPasswordFinalFormValidator bean = applicationContext.getBean(RenewPasswordFinalFormValidator.class);
    		binder.addValidators(bean);
    	}
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
