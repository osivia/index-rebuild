/**
 *
 */
package fr.index.cloud.ens.directory.person.creation.portlet.service;

import fr.index.cloud.ens.directory.person.creation.portlet.model.PersonCreationForm;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterException;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;
import org.osivia.portal.api.tasks.ITasksService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.text.Normalizer;
import java.util.*;

/**
 * @author Loïc Billon
 *
 */
@Service
public class PersonCreationServiceImpl implements PersonCreationService {

    /** Base 62. */
    private static final int BASE_62 = 62;

    /** Base 62 alphabet conversion table. */
    private static final char[] TO_BASE_62 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};


    /** Logger. */
    private static final Log logger = LogFactory.getLog(PersonCreationServiceImpl.class);


    @Autowired
    private PersonUpdateService personService;

    /** Portal URL factory. */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /** Forms service. */
    @Autowired
    private IFormsService formsService;

    /** Bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;

    /** Notifications service. */
    @Autowired
    private INotificationsService notificationsService;

    /** User preferences service. */
    @Autowired
    private UserPreferencesService userPreferencesService;

    
    /** Tasks service. */
    @Autowired
    private ITasksService tasksService;

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

    /* (non-Javadoc)
     * @see fr.index.cloud.ens.directory.person.creation.portlet.controller.PersonCreationService#proceedInit(org.osivia.portal.api.context.PortalControllerContext, fr.index.cloud.ens.directory.person.creation.portlet.controller.PersonCreationForm)
     */
    @Override
    public void proceedInit(PortalControllerContext portalControllerContext, PersonCreationForm form) {
        // A person can be in directory but his account should not be valid
        Person searchByMail = this.personService.getEmptyPerson();
        searchByMail.setMail(form.getMail());
        List<Person> findByCriteria = personService.findByCriteria(searchByMail);

        Person person;
        String uid;
        boolean reuseAccount = false;
        if (findByCriteria.size() > 0) {

            Person personFound = findByCriteria.get(0);
            person = personFound;
            uid = person.getUid();
            reuseAccount = true;

        } else {
            person = this.personService.getEmptyPerson();
            uid = this.generateUid();
            person.setUid(uid);
            // Person is created with passed current date validity. 
            // It can not be logged in until the mail is checked, which will remove this attribute.
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            person.setValidity(new Date(cal.getTimeInMillis()));
        }

        person.setGivenName(form.getFirstname());
        person.setSn(form.getLastname());
        person.setMail(form.getMail());
        person.setDisplayName(form.getNickname());
        person.setCn(person.getGivenName() + " " + person.getSn());

        if (reuseAccount) {
            this.personService.update(person);
        } else {
            this.personService.create(person);

        }

        // Record password
        this.personService.updatePassword(person, form.getNewpassword());


        // Variables
        Map<String, String> variables = new HashMap<>();
        variables.put("uid", uid);
        variables.put("mail", form.getMail());
        variables.put("termsOfService", this.getPortalTermsOfService(portalControllerContext));

        // Start
        try {
            formsService.start(portalControllerContext, MODEL_ID, variables);
        } catch (PortalException | FormFilterException e) {

            logger.error("Error during person creation", e);

            Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

            String message = bundle.getString("createaccount.mail.error");
            this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.ERROR);

        }

    }


    /**
     * Get portal terms of service.
     *
     * @param portalControllerContext portal controller context
     * @return terms of service
     */
    private String getPortalTermsOfService(PortalControllerContext portalControllerContext) {
       	String    termsOfService = System.getProperty("osivia.services.cgu.level");

        return termsOfService;
    }


    /**
     * Generate person identifier.
     *
     * @return identifier
     */
    private String generateUid() {
        // Random generator
        Random random = new Random();

        // Search criteria
        Person criteria = this.personService.getEmptyPerson();
        // Search results
        List<Person> results = null;

        // Person identifier
        String uid = null;

        while (StringUtils.isEmpty(uid) || CollectionUtils.isNotEmpty(results)) {
            // Generate new identifier
            Character[] array = new Character[PERSON_UID_LENGTH];
            for (int i = 0; i < PERSON_UID_LENGTH; i++) {
                array[i] = TO_BASE_62[random.nextInt(BASE_62)];
            }
            uid = StringUtils.join(array);

            // Check identifier uniqueness
            criteria.setUid(uid);
            results = this.personService.findByCriteria(criteria);
        }

        return uid;
    }


    public String removeAccents(String input) {
        // Suppression d'accents
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        return input.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }


    @Override
    public void proceedRegistration(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        // Person identifier
        String uid = window.getProperty("uid");
        // Accepted terms of service
        String termsOfService = window.getProperty("termsOfService");


        // Person
        Person person = personService.getPerson(uid);

        // Account is valid unlimited time
        person.setValidity(null);

        personService.update(person);


        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext, uid);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // Save user preferences
        userPreferences.setTermsOfService(termsOfService);
        userPreferences.setUpdated(true);
        try {
            this.userPreferencesService.saveUserPreferences(portalControllerContext, userPreferences);
        } catch (PortalException e) {
            throw new PortletException(e);
        }
  

         // Nuxeo controller
         NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
         Document task = nuxeoController.getDocumentContext( nuxeoController.getContentPath()).getDenormalizedDocument();
     
        // Close procedure
        try {
            this.formsService.proceed(portalControllerContext, task, "verify", null);
        } catch (PortalException | FormFilterException e) {
            throw new PortletException(e);
        }
    }


    @Override
    public String getTermsOfServiceUrl(PortalControllerContext portalControllerContext) throws PortletException {
 
        // Terms of service path
        String path = System.getProperty("osivia.services.cgu.path");


        // Terms of service URL
        String url;
        if (StringUtils.isEmpty(path)) {
            url = null;
        } else {
            // Portlet instance
            String instance = "toutatice-portail-cms-nuxeo-viewFragmentPortletInstance";

            // Window properties
            Map<String, String> properties = new HashMap<>();
            properties.put(Constants.WINDOW_PROP_URI, path);
            properties.put("osivia.fragmentTypeId", "html_property");
            properties.put("osivia.propertyName", "note:note");

            try {
                url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, instance, properties, PortalUrlType.MODAL);
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        }

        return url;
    }

}
