package fr.index.cloud.ens.mutualization.copy.portlet.service;

import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyConfirmationForm;
import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyForm;
import fr.index.cloud.ens.mutualization.copy.portlet.repository.MutualizationCopyRepository;
import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.path.IBrowserService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mutualization copy portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see MutualizationCopyService
 */
@Service
public class MutualizationCopyServiceImpl implements MutualizationCopyService {

    /**
     * Duplicate choice identifier.
     */
    private static final String DUPLICATE_CHOICE_ID = "_duplicate_";


    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portlet repository.
     */
    @Autowired
    private MutualizationCopyRepository repository;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /**
     * Browser service.
     */
    @Autowired
    private IBrowserService browserService;

    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;

    /**
     * Notifications service.
     */
    @Autowired
    private INotificationsService notificationsService;

    /**
     * Document DAO.
     */
    @Autowired
    private DocumentDAO documentDao;


    /**
     * Constructor.
     */
    public MutualizationCopyServiceImpl() {
        super();
    }


    @Override
    public MutualizationCopyForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        // Form
        MutualizationCopyForm form = this.applicationContext.getBean(MutualizationCopyForm.class);

        if (StringUtils.isEmpty(form.getDocumentPath())) {
            // Document path
            String documentPath = window.getProperty(DOCUMENT_PATH_WINDOW_PROPERTY);
            form.setDocumentPath(documentPath);
        }

        if (StringUtils.isEmpty(form.getBasePath())) {
            // User workspace
            Document userWorkspace = this.repository.getUserWorkspace(portalControllerContext);

            // User workspace base path
            String basePath;
            if (userWorkspace == null) {
                basePath = null;
            } else {
                basePath = userWorkspace.getPath() + "/documents";
            }
            form.setBasePath(basePath);
        }

        return form;
    }


    @Override
    public boolean alreadyExists(PortalControllerContext portalControllerContext, MutualizationCopyForm form) throws PortletException {
        // Existing targets
        List<Document> existingTargets = this.repository.getExistingTargets(portalControllerContext, form);

        return CollectionUtils.isNotEmpty(existingTargets);
    }


    @Override
    public void copy(PortalControllerContext portalControllerContext, MutualizationCopyForm form, MutualizationCopyConfirmationForm confirmationForm) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Choice
        String choice;
        if (confirmationForm == null) {
            choice = null;
        } else {
            choice = confirmationForm.getChoice();
        }

        if (StringUtils.isEmpty(choice) || DUPLICATE_CHOICE_ID.equals(choice)) {
            this.repository.copy(portalControllerContext, form);
        } else {
            this.repository.replace(portalControllerContext, form, choice);
        }

        // Notification
        String message = bundle.getString("MUTUALIZATION_COPY_MESSAGE_SUCCESS");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);
    }


    @Override
    public String getRedirectionUrl(PortalControllerContext portalControllerContext, MutualizationCopyForm form) {
        return this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
    }


    @Override
    public String browse(PortalControllerContext portalControllerContext) throws PortletException {
        // Data
        String data;
        try {
            data = this.browserService.browse(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return data;
    }


    @Override
    public MutualizationCopyConfirmationForm getConfirmationForm(PortalControllerContext portalControllerContext) throws PortletException {
        // Form
        MutualizationCopyForm form = this.getForm(portalControllerContext);

        // Confirmation form
        MutualizationCopyConfirmationForm confirmationForm = this.applicationContext.getBean(MutualizationCopyConfirmationForm.class);

        // Location
        if (StringUtils.isNotEmpty(form.getTargetPath())) {
            Document document = this.repository.getDocument(portalControllerContext, form.getTargetPath());
            DocumentDTO documentDto = this.documentDao.toDTO(document);
            confirmationForm.setLocation(documentDto);
        }

        return confirmationForm;
    }


    @Override
    public Map<String, String> getConfirmationChoices(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());


        // Form
        MutualizationCopyForm form = this.getForm(portalControllerContext);

        // Existing targets
        List<Document> targets = this.repository.getExistingTargets(portalControllerContext, form);


        // Choices
        Map<String, String> choices = new LinkedHashMap<>(targets.size() + 1);

        // Duplicate
        choices.put(DUPLICATE_CHOICE_ID, bundle.getString("MUTUALIZATION_COPY_CONFIRMATION_CHOICE_DUPLICATE"));

        // Replace
        if (targets.size() == 1) {
            Document target = targets.get(0);
            choices.put(target.getId(), bundle.getString("MUTUALIZATION_COPY_CONFIRMATION_CHOICE_REPLACE"));
        } else {
            for (Document target : targets) {
                choices.put(target.getId(), bundle.getString("MUTUALIZATION_COPY_CONFIRMATION_CHOICE_REPLACE_DETAIL", target.getTitle()));
            }
        }

        return choices;
    }

}
