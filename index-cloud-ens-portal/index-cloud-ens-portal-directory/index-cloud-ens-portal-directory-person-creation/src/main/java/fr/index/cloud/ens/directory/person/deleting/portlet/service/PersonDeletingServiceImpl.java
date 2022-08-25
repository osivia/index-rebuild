package fr.index.cloud.ens.directory.person.deleting.portlet.service;

import fr.index.cloud.ens.directory.person.deleting.portlet.model.PersonDeletingForm;
import fr.index.cloud.ens.directory.person.deleting.portlet.repository.PersonDeletingRepository;
import org.apache.commons.lang.BooleanUtils;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.portalobject.bridge.PortalObjectUtils;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Person deleting portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see PersonDeletingService
 */
@Service
public class PersonDeletingServiceImpl implements PersonDeletingService {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portlet repository.
     */
    @Autowired
    private PersonDeletingRepository repository;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;

    /**
     * Person service.
     */
    @Autowired
    private PersonUpdateService personService;


    /**
     * Constructor.
     */
    public PersonDeletingServiceImpl() {
        super();
    }


    @Override
    public String getViewPath(PortalControllerContext portalControllerContext) {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        // View form indicator
        boolean viewForm = BooleanUtils.toBoolean(window.getProperty(VIEW_FORM_WINDOW_PROPERTY));

        // View path
        String viewPath;
        if (viewForm) {
            viewPath = "form";
        } else {
            viewPath = "button";
        }

        return viewPath;
    }


    @Override
    public PersonDeletingForm getForm(PortalControllerContext portalControllerContext) {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Current person
        Person person = (Person) request.getAttribute(Constants.ATTR_LOGGED_PERSON_2);

        // Form
        PersonDeletingForm form = this.applicationContext.getBean(PersonDeletingForm.class);
        form.setPerson(person);

        return form;
    }


    @Override
    public String getViewFormUrl(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put("osivia.title", bundle.getString("deleting.title"));
        properties.put("osivia.back.reset", String.valueOf(true));
        properties.put(VIEW_FORM_WINDOW_PROPERTY, String.valueOf(true));

        // URL
        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, PORTLET_INSTANCE, properties);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    @Override
    public String getCancelUrl(PortalControllerContext portalControllerContext) {
        UniversalID myAccountDocumentId = new UniversalID( "idx", "DEFAULT_MON-COMPTE");
        String  url = this.portalUrlFactory.getViewContentUrl(portalControllerContext, myAccountDocumentId);

        return url;
    }


    @Override
    public void validate(PortalControllerContext portalControllerContext, PersonDeletingForm form) {
        // Update model
        form.setValidated(true);
    }


    @Override
    public void delete(PortalControllerContext portalControllerContext, PersonDeletingForm form) throws PortletException {
        if (form.isValidated()) {
            // Delete user workspace
            this.repository.deleteUserWorkspace(portalControllerContext);
            // Delete user
            this.personService.delete(form.getPerson());
        }
    }

}
