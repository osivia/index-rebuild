/**
 * 
 */
package fr.index.cloud.ens.directory.person.renew.portlet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import fr.index.cloud.ens.directory.person.renew.portlet.controller.RenewPasswordForm;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterException;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;

/**
 * @author Loïc Billon
 *
 */
@Service
public class RenewPasswordServiceImpl implements RenewPasswordService {


    private static String USER_MAIL = "mail";


    /** Forms service. */
    @Autowired
    private IFormsService formsService;

    /** Bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;

    /** Notifications service. */
    @Autowired
    private INotificationsService notificationsService;
    
    /** Person service. */
    @Autowired
    private PersonUpdateService personService;    

    /** Log. */
    private final Log log = LogFactory.getLog(this.getClass());


	/* (non-Javadoc)
	 * @see fr.index.cloud.ens.directory.person.creation.portlet.controller.RenewPasswordService#proceedInit(org.osivia.portal.api.context.PortalControllerContext, fr.index.cloud.ens.directory.person.creation.portlet.controller.RenewPasswordForm)
	 */
	@Override
	public void proceedInit(PortalControllerContext portalControllerContext, RenewPasswordForm form) {
		
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

        try {

            // Variables
            Map<String, String> variables = new HashMap<>();

            variables.put(USER_MAIL, form.getMail());

            // Start
            this.formsService.start(portalControllerContext, MODEL_ID, variables);

            
        } catch (PortalException | FormFilterException e) {
            // Error notification
            String message = bundle.getString("MESSAGE_SEND_ERROR");
            this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.ERROR);

            this.log.error(message, e);
        }
	}

	/* (non-Javadoc)
	 * @see fr.index.cloud.ens.directory.person.creation.portlet.controller.RenewPasswordService#proceedPassword(org.osivia.portal.api.context.PortalControllerContext, fr.index.cloud.ens.directory.person.creation.portlet.controller.RenewPasswordForm)
	 */
	@Override
	public void proceedPassword(PortalControllerContext portalControllerContext, RenewPasswordForm form) {
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        try {

    		Person criteria = personService.getEmptyPerson();
    		criteria.setMail(form.getMail());
    		List<Person> persons = personService.findByCriteria(criteria);
    		Person person = persons.get(0);

            this.personService.updatePassword(person, form.getNewpassword());   
        	
            // Variables
            Map<String, String> variables = new HashMap<>(); 

            Document task = nuxeoController.getDocumentContext( nuxeoController.getContentPath()).getDenormalizedDocument();
            this.formsService.proceed(portalControllerContext, task, variables);


        } catch (Exception  e) {
            // Error notification
            String message = bundle.getString("renew.send.error");
            this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.ERROR);

            this.log.error(message, e);
        }
	}
	
    
	@Override
	public void validatePasswordRules(Errors errors, String field, String password) {
		Map<String, String> messages = this.personService.validatePasswordRules(password);

		if (MapUtils.isNotEmpty(messages)) {
			for (Map.Entry<String, String> entry : messages.entrySet()) {
				errors.rejectValue(field, entry.getKey(), entry.getValue());
			}
		}
	}    	

	/* (non-Javadoc)
	 * @see fr.index.cloud.ens.directory.person.renew.portlet.service.RenewPasswordService#getPasswordRulesInformation(org.osivia.portal.api.context.PortalControllerContext, java.lang.String)
	 */
	@Override
	public Element getPasswordRulesInformation(PortalControllerContext portalControllerContext, String password) {
		// Information
		Map<String, Boolean> information = this.personService.getPasswordRulesInformation(password);

		// Container
		Element container = DOM4JUtils.generateDivElement(StringUtils.EMPTY);

		if (MapUtils.isNotEmpty(information)) {
			Element ul = DOM4JUtils.generateElement("ul", "list-unstyled", StringUtils.EMPTY);
			container.add(ul);

			for (Map.Entry<String, Boolean> entry : information.entrySet()) {
				Element li = DOM4JUtils.generateElement("li", null, StringUtils.EMPTY);
				ul.add(li);

				String htmlClass;
				String icon;
				if (BooleanUtils.isTrue(entry.getValue())) {
					htmlClass = "text-success";
					icon = "glyphicons glyphicons-check";
				} else {
					htmlClass = null;
					icon = "glyphicons glyphicons-unchecked";
				}
				Element item = DOM4JUtils.generateElement("span", htmlClass, entry.getKey(), icon, null);
				li.add(item);
			}
		}

		return container;
	}
	

}
