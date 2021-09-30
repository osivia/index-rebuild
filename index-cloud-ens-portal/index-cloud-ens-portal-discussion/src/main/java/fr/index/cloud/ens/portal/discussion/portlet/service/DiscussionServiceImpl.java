package fr.index.cloud.ens.portal.discussion.portlet.service;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.tasks.ITasksService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import fr.index.cloud.ens.portal.discussion.portlet.model.DetailForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsFormSort;
import fr.index.cloud.ens.portal.discussion.portlet.model.Options;
import fr.index.cloud.ens.portal.discussion.portlet.model.comparator.DiscussionComparator;
import fr.index.cloud.ens.portal.discussion.portlet.repository.DiscussionRepository;
import fr.index.cloud.ens.portal.discussion.util.ApplicationContextProvider;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoServiceFactory;

/**
 * Discussion portlet service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see DiscussionService
 * @see ApplicationContextAware
 */
@Service
public class DiscussionServiceImpl implements DiscussionService, ApplicationContextAware {

    private static final String ADMINISTRATORS = "Administrators";

    /**
     * Portlet repository.
     */
    @Autowired
    private DiscussionRepository repository;

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
     * Notifications service.
     */
    @Autowired
    private INotificationsService notificationsService;


    /**
     * Application context.
     */
    private ApplicationContext applicationContext;
    
    
    /**
     * Person service.
     */
    @Autowired
    public ITasksService tasksService;


    /** The logger. */
    protected static Log logger = LogFactory.getLog(DiscussionServiceImpl.class);
    
    /** model identifier. */
    public static final String MODEL_ID = IFormsService.FORMS_WEB_ID_PREFIX + "report-message";
    
    /**
     * Constructor.
     */
    public DiscussionServiceImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DiscussionsForm getDiscussionsForm(PortalControllerContext portalControllerContext) throws PortletException {
        // Discussions form
        DiscussionsForm form = this.applicationContext.getBean(DiscussionsForm.class);

        if (!form.isLoaded()) {
            // Discussion documents
            List<DiscussionDocument> discussionDocuments = this.repository.getDiscussions(portalControllerContext);
            form.setDocuments(discussionDocuments);

            // Sort
            if (form.getSort() == null) {
                this.sort(portalControllerContext, form, DiscussionsFormSort.DOCUMENT, false);
            } else {
                this.sort(portalControllerContext, form, form.getSort(), form.isAlt());
            }

            form.setLoaded(true);
        }

        return form;
    }


    /**
     * {@inheritDoc}
     */

    private DetailForm getDetailForm(PortalControllerContext portalControllerContext, Options options) throws PortletException {
        // Discussion form
        DetailForm form = this.applicationContext.getBean(DetailForm.class);

        if (!form.isLoaded()) {

         // today    
            Calendar date = new GregorianCalendar();
            // reset hour, minutes, seconds and millis
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            
            form.setToday(date.getTime());
            
            form.setId(options.getId());
            form.setAuthor(portalControllerContext.getRequest().getRemoteUser());
            if( options.getMessageId() != null)
                form.setAnchor("msg-"+ options.getMessageId());
            else
                form.setAnchor("last-message");  
            form.setOptions(options);

            updateModel(portalControllerContext, form);

            form.setLoaded(true);
        }

        return form;
    }

    
    @Override
    public DetailForm getDetailForm(PortalControllerContext portalControllerContext, String mode, String id, String participant, String publicationId,  String messageId) throws PortletException {
        // Discussion form
        
        Options options = (Options) portalControllerContext.getRequest().getPortletSession().getAttribute("options");
        
    
        // if id changes, options must be renewed (ie access from list)
        if( options == null || ((id != null) && (!id.equals(options.getId())))) {
            options = new Options();
            
            portalControllerContext.getRequest().getPortletSession().setAttribute("options", options);
           
             Boolean isAdministrator = (Boolean) portalControllerContext.getRequest().getAttribute(InternalConstants.ADMINISTRATOR_INDICATOR_ATTRIBUTE_NAME);
    
            if( BooleanUtils.isTrue(isAdministrator))
                options.setAdministrator(true);
            else
                options.setAdministrator(false);
    
            
            if( Options.MODE_ADMIN.equals(mode) && options.isAdministrator())
                options.setMode(mode );    
            
            options.setId(id);
            options.setParticipant(participant);
            options.setPublicationId(publicationId);
            options.setMessageId(messageId);

        }
        
        return getDetailForm(portalControllerContext, options);
    }   
    

    /**
     * Update model.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    
    private void updateModel(PortalControllerContext portalControllerContext, DetailForm form) throws PortletException {


        DiscussionDocument doc = null;

        if (form.getId() != null) {
            // Reading by id (default)
            doc = this.repository.getDiscussion(portalControllerContext, form);
        } else if (form.getOptions().getParticipant() != null) {
            // Reading by participant
            List<String> participants = new ArrayList<String>();
            participants.add(portalControllerContext.getRequest().getRemoteUser());
            participants.add(form.getOptions().getParticipant());
            doc = this.repository.getDiscussionByParticipant(portalControllerContext, form.getOptions().getParticipant(), form.getOptions().getPublicationId(), false);
            form.setId(doc.getWebId());
        } else if (form.getOptions().getPublicationId()  != null) {
            doc = this.repository.getDiscussionByPublication(portalControllerContext, form.getOptions().getPublicationId(), false);
            form.setId(doc.getWebId());            
        }
        
        // Check security after document has been loaded
        if( doc != null) {
            boolean checked = false;
            
            if( form.getOptions().isAdministrator())  {
                checked = true;
            }
            
            List<String> participants = doc.getParticipants();
            if( participants != null && participants.contains(portalControllerContext.getRequest().getRemoteUser()))   {
                checked = true;
            }
            
            if( StringUtils.equals(doc.getType(), DiscussionDocument.TYPE_USER_COPY)    ) {
                try {
                    if(this.repository.getDiscussionsPubInfosByCopy(portalControllerContext).containsKey(doc.getTarget())) {
                        checked = true;
                    }
                } catch (PortalException e) {
                    throw new PortletException(e);
                }
            }
            
             if( !checked)  {   
                // Internationalization bundle
                Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());                
                throw new SecurityException(bundle.getString("ERROR_403"));
            }
        }
        

        // update read preference
        if (form.getId() != null) {
            repository.checkUserReadPreference(portalControllerContext, form.getId(), doc.getMessages().size());
        }
        
        // update cache
        if (form.getId() != null) {
            repository.checkUserReadPreference(portalControllerContext, form.getId(), doc.getMessages().size());
            
            try {
                tasksService.resetTasksCount(portalControllerContext);
            } catch (PortalException e) {
               throw new PortletException(e);
            }
        }


        form.setDocument(doc);


    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sort(PortalControllerContext portalControllerContext, DiscussionsForm form, DiscussionsFormSort sort, boolean alt) {
        if (CollectionUtils.isNotEmpty(form.getDocuments())) {
            // Comparator
            Comparator<DiscussionDocument> comparator = this.applicationContext.getBean(DiscussionComparator.class, sort, alt);

            form.getDocuments().sort(comparator);

            // Update model
            form.setSort(sort);
            form.setAlt(alt);
        }
    }





    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDiscussions(PortalControllerContext portalControllerContext, DiscussionsForm form, String[] identifiers) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        
        // Action response
        ActionResponse response = (ActionResponse) portalControllerContext.getResponse();
        
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Selected documents
        List<DiscussionDocument> selection = this.getSelection(form, identifiers);

        this.repository.markAsDeleted(portalControllerContext, selection);
        
        

        // Update model
        this.updateModel(portalControllerContext, form, selection);
        
        // Notification
        String message = bundle.getString( "DISCUSSION_DELETE_SELECTIONS_MESSAGE_SUCCESS");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);     
        
        String url= this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new PortletException(e);
        }        
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDiscussion(PortalControllerContext portalControllerContext, DetailForm form) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        
        // Action response
        ActionResponse response = (ActionResponse) portalControllerContext.getResponse();
        
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Selected documents
        List<DiscussionDocument> selection = new ArrayList<>();
        selection.add(form.getDocument());

        this.repository.markAsDeleted(portalControllerContext, selection);

        // Update model
        updateModel(portalControllerContext,  form);
        
        // Notification
        String message = bundle.getString( "DISCUSSION_DELETE_CURRENT_MESSAGE_SUCCESS");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);        
        
        
        String url= this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new PortletException(e);
        }
        
    }

    
    
    

    /**
     * Get selected documents.
     *
     * @param form discussion form
     * @param identifiers selected document identifiers
     * @return selected documents
     */
    private List<DiscussionDocument> getSelection(DiscussionsForm form, String[] identifiers) {
        // Discussion documents map
        Map<String, DiscussionDocument> discussionDocumentMap;
        if (CollectionUtils.isEmpty(form.getDocuments())) {
            discussionDocumentMap = null;
        } else {
            discussionDocumentMap = new HashMap<>(form.getDocuments().size());
            for (DiscussionDocument discussion : form.getDocuments()) {
                discussionDocumentMap.put(discussion.getId(), discussion);
            }
        }


        // Selected documents
        List<DiscussionDocument> selection;
        if (ArrayUtils.isEmpty(identifiers) || MapUtils.isEmpty(discussionDocumentMap)) {
            selection = null;
        } else {
            selection = new ArrayList<>(identifiers.length);
            for (String identifier : identifiers) {
                DiscussionDocument discussion = discussionDocumentMap.get(identifier);
                if (discussion != null) {
                    selection.add(discussion);
                }
            }
        }

        return selection;
    }



    /**
     * Update model.
     *
     * @param portalControllerContext portal controller context
     * @param form discussion form
     * @param selection selected documents
     * @param rejected rejected documents
     * @param bundle internationalization bundle
     * @param messagePrefix message prefix
     */
    private void updateModel(PortalControllerContext portalControllerContext, DiscussionsForm form, List<DiscussionDocument> selection) throws PortletException {

        // Discussion documents
        List<DiscussionDocument> discussions = this.repository.getDiscussions(portalControllerContext);
        form.setDocuments(discussions);

        // Sort
        if (form.getSort() == null) {
            this.sort(portalControllerContext, form, DiscussionsFormSort.DOCUMENT, false);
        } else {
            this.sort(portalControllerContext, form, form.getSort(), form.isAlt());
        }


        // Update model
        form.getDocuments().removeAll(selection);
    }


    @Override
    public Element getToolbar(PortalControllerContext portalControllerContext, List<String> indexes, DiscussionsForm form) throws PortletException {
        
        long  begin = System.currentTimeMillis();

        
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

        // Toolbar container
        Element container = DOM4JUtils.generateDivElement(null);

        // Toolbar
        Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar", AccessibilityRoles.TOOLBAR);
        container.add(toolbar);

        if (CollectionUtils.isNotEmpty(indexes)) {


            // Discussions documents
            List<DiscussionDocument> documents = form.getDocuments();

            // Selection
            List<DiscussionDocument> selection = new ArrayList<>(indexes.size());
            for (String index : indexes) {
                int i = NumberUtils.toInt(index, -1);
                if ((i > -1) && (i < documents.size())) {
                    DiscussionDocument document = documents.get(i);
                    if ((document != null) && StringUtils.isNotEmpty(document.getId())) {
                        selection.add(document);
                    }
                }
            }

            if (indexes.size() == selection.size()) {
                // Selection identifiers
                String[] identifiers = new String[selection.size()];
                for (int i = 0; i < selection.size(); i++) {
                    DiscussionDocument document = selection.get(i);
                    identifiers[i] = document.getId();
                }

                // Delete
                Element deleteButton = this.getDeleteToolbarButton(portalControllerContext, bundle);
                toolbar.add(deleteButton);
                Element deleteModalConfirmation = this.getDeleteModalConfirmation(portalControllerContext, bundle, identifiers);
                container.add(deleteModalConfirmation);
            }
        }
        
        
        if( logger.isDebugEnabled())    {
            long end = System.currentTimeMillis();
            
            logger.debug("getToolbar : elapsed time = " + (end- begin) + " ms.");
        }

        return container;
    }


    /**
     * Get delete selection toolbar button DOM element.
     *
     * @param portalControllerContext portal controller context
     * @param bundle internationalization bundle
     * @return DOM element
     */
    private Element getDeleteToolbarButton(PortalControllerContext portalControllerContext, Bundle bundle) {
        // Portlet response
        PortletResponse response = portalControllerContext.getResponse();

        // Modal identifier
        String id = response.getNamespace() + "-delete";

        // Text
        String text = bundle.getString("DISCUSSION_TOOLBAR_DELETE_SELECTION");
        // Icon
        String icon = "glyphicons glyphicons-basic-bin";

        return this.getToolbarButton(id, text, icon);
    }


    /**
     * Get toolbar button DOM element.
     *
     * @param id modal identifier
     * @param title button text
     * @param icon button icon
     * @return DOM element
     */
    private Element getToolbarButton(String id, String title, String icon) {
        // URL
        String url = "#" + id;
        // HTML classes
        String htmlClass = "btn btn-link btn-link-hover-primary text-primary-dark btn-sm mr-1 no-ajax-link";

        // Button
        Element button = DOM4JUtils.generateLinkElement(url, null, null, htmlClass, null, icon);
        DOM4JUtils.addDataAttribute(button, "toggle", "modal");

        // Text
        Element text = DOM4JUtils.generateElement("span", "d-none d-xl-inline", title);
        button.add(text);

        return button;
    }


    /**
     * Get delete modal confirmation DOM element.
     *
     * @param portalControllerContext portal controller context
     * @param bundle internationalization bundle
     * @param identifiers selection identifiers
     * @return DOM element
     */
    private Element getDeleteModalConfirmation(PortalControllerContext portalControllerContext, Bundle bundle, String[] identifiers) {
        // Portlet response
        PortletResponse response = portalControllerContext.getResponse();

        // Action
        String action = "delete";
        // Modal identifier
        String id = response.getNamespace() + "-delete";
        // Modal title
        String title = bundle.getString("DISCUSSION_DELETE_SELECTION_MODAL_TITLE");
        // Modal message
        String message = bundle.getString("DISCUSSION_DELETE_SELECTION_MODAL_MESSAGE");

        return this.getModalConfirmation(portalControllerContext, bundle, action, identifiers, id, title, message);
    }


    /**
     * Get modal confirmation DOM element.
     *
     * @param portalControllerContext portal controller context
     * @param bundle internationalization bundle
     * @param action confirmation action name
     * @param identifiers selection identifiers
     * @param id modal identifier
     * @param title modal title
     * @param message modal message
     * @return DOM element
     */
    private Element getModalConfirmation(PortalControllerContext portalControllerContext, Bundle bundle, String action, String[] identifiers, String id,
            String title, String message) {
        // Portlet response
        PortletResponse portletResponse = portalControllerContext.getResponse();
        // MIME response
        MimeResponse mimeResponse;
        if (portletResponse instanceof MimeResponse) {
            mimeResponse = (MimeResponse) portletResponse;
        } else {
            mimeResponse = null;
        }


        // Modal
        Element modal = DOM4JUtils.generateDivElement("modal fade");
        DOM4JUtils.addAttribute(modal, "id", id);

        // Modal dialog
        Element modalDialog = DOM4JUtils.generateDivElement("modal-dialog");
        modal.add(modalDialog);

        // Modal content
        Element modalContent = DOM4JUtils.generateDivElement("modal-content");
        modalDialog.add(modalContent);

        // Modal header
        Element modalHeader = DOM4JUtils.generateDivElement("modal-header");
        modalContent.add(modalHeader);

        // Modal title
        Element modalTitle = DOM4JUtils.generateElement("h5", "modal-title", title);
        modalHeader.add(modalTitle);

        // Modal close button
        Element close = DOM4JUtils.generateElement("button", "close", "×");
        DOM4JUtils.addAttribute(close, "type", "button");
        DOM4JUtils.addDataAttribute(close, "dismiss", "modal");
        modalHeader.add(close);

        // Modal body
        Element modalBody = DOM4JUtils.generateDivElement("modal-body");
        modalContent.add(modalBody);

        // Modal body content
        Element bodyContent = DOM4JUtils.generateElement("p", null, message);
        modalBody.add(bodyContent);

        // Modal footer
        Element modalFooter = DOM4JUtils.generateDivElement("modal-footer");
        modalContent.add(modalFooter);

        // Cancel button
        Element cancel = DOM4JUtils.generateElement("button", "btn btn-secondary", bundle.getString("CANCEL"), null, null);
        DOM4JUtils.addAttribute(cancel, "type", "button");
        DOM4JUtils.addDataAttribute(cancel, "dismiss", "modal");
        modalFooter.add(cancel);

        // Confirmation button
        String url;
        if (mimeResponse == null) {
            url = "#";
        } else {
            // Action URL
            PortletURL actionUrl = mimeResponse.createActionURL();
            actionUrl.setParameter(ActionRequest.ACTION_NAME, action);
            actionUrl.setParameter("identifiers", identifiers);

            url = actionUrl.toString();
        }
        Element confirm = DOM4JUtils.generateLinkElement(url, null, null, "btn btn-warning", bundle.getString("CONFIRM"), null);
        modalFooter.add(confirm);

        return modal;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ApplicationContextProvider.setApplicationContext(applicationContext);
    }


    @Override
    public void createDiscussion(PortalControllerContext portalControllerContext, Options options, DiscussionCreation discution) throws PortletException {
        
        Document document = this.repository.createDiscussion(portalControllerContext, discution);
        options.setId(document.getProperties().getString("ttc:webid"));

    }


    @Override
    public void addMessage(PortalControllerContext portalControllerContext, DetailForm form) throws PortletException {
        

        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());


        // Create discussion if necessary
        if (form.getId() == null) {
            if (StringUtils.isNotEmpty(form.getOptions().getParticipant())) {
                saveNewDiscussionByParticpant(portalControllerContext, form);
            } else if( StringUtils.isNotEmpty(form.getOptions().getPublicationId() )) {
                saveNewDiscussionByPublication(portalControllerContext, form);
            } else
                throw new PortletException("no participant nor publication. Can't create discussion");

        }

        // Add new Message
        this.repository.addMessage(portalControllerContext, form);

        updateModel(portalControllerContext, form);

        form.setNewMessage(null);
        form.setAnchor("last-message");

        // Notification
        String message = bundle.getString("DISCUSSION_MESSAGE_SUCCESS_ADD_NEW_MESSAGE");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

    }


    /**
     * Save the discussion by participant.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    
    private void saveNewDiscussionByParticpant(PortalControllerContext portalControllerContext, DetailForm form) throws PortletException {

        DiscussionCreation discussion = new DiscussionCreation();
        List<String> participants = new ArrayList<>();
        participants.add(form.getOptions().getParticipant());
        participants.add(portalControllerContext.getRequest().getRemoteUser());
        discussion.setParticipants(participants);
        
        discussion.setTarget(form.getOptions().getPublicationId());

        createDiscussion(portalControllerContext, form.getOptions(), discussion);
        form.setId(form.getOptions().getId());
        
        updateModel(portalControllerContext, form);
    }


    /**
     * Save the discussion by participant.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    
    private void saveNewDiscussionByPublication(PortalControllerContext portalControllerContext, DetailForm form) throws PortletException {

        DiscussionCreation discussion = new DiscussionCreation();
        discussion.setType("USER_COPY");
        discussion.setTarget(form.getOptions().getPublicationId() );

        createDiscussion(portalControllerContext, form.getOptions(), discussion);
        form.setId(form.getOptions().getId());        
        
        updateModel(portalControllerContext, form);
    }

    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteMessage(PortalControllerContext portalControllerContext, DetailForm form, String messageId) throws PortletException {
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

        this.repository.deleteMessage(portalControllerContext, form, messageId);

        updateModel(portalControllerContext, form);

        // Notification
        String message = bundle.getString("DISCUSSION_MESSAGE_SUCCESS_DELETE_MESSAGE");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void reportMessage(PortalControllerContext portalControllerContext, DetailForm form, String messageId) throws PortletException {
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

        Map<String, String> variables = new HashMap<>();
       
        variables.put("discussionId",  form.getId());
        variables.put("messageId",  messageId); 
        variables.put("userId",  portalControllerContext.getRequest().getRemoteUser()); 
  

        try {
            variables = NuxeoServiceFactory.getFormsService().start(portalControllerContext, MODEL_ID, variables);
        } catch (Exception e) {
            System.out.println(e);
        }

       
        // Notification
        String message = bundle.getString("DISCUSSION_MESSAGE_SUCCESS_REPORT_MESSAGE");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

    }

}
