package fr.index.cloud.ens.portal.discussion.unread.configuration.services;


import fr.index.cloud.ens.portal.discussion.portlet.model.DetailForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionMessage;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsFormSort;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;
import fr.index.cloud.ens.portal.discussion.portlet.model.comparator.DiscussionComparator;
import fr.index.cloud.ens.portal.discussion.portlet.repository.DiscussionRepository;
import fr.index.cloud.ens.portal.discussion.portlet.repository.DiscussionRepositoryImpl;
import fr.index.cloud.ens.portal.discussion.unread.configuration.model.DiscussionsUnreadMessages;
import fr.index.cloud.ens.portal.discussion.unread.configuration.model.Message;
import fr.index.cloud.ens.portal.discussion.util.ApplicationContextProvider;
import fr.toutatice.portail.cms.nuxeo.api.discussions.DiscussionHelper;
import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.menubar.MenubarGroup;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.tasks.CustomTask;
import org.osivia.portal.api.tasks.ITasksService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.portlet.*;
import java.util.*;

/**
 * Discussion portlet service implementation.
 *
 * @author Jean-Sébastien Steux
 * @see UnreadService
 * @see ApplicationContextAware
 */
@Service
public class UnreadServiceImpl implements UnreadService, ApplicationContextAware {

    /**
     * Portlet repository.
     */
    @Autowired
    private DiscussionRepository repository;

    /** Person service. */
    @Autowired
    private PersonService personService;




    /**
     * Application context.
     */
    private ApplicationContext applicationContext;
    

    /** The logger. */
    protected static Log logger = LogFactory.getLog(UnreadServiceImpl.class);
    
    private DocumentDAO documentDAO;
    
    /**
     * Constructor.
     */
    public UnreadServiceImpl() {
        super();
        
        documentDAO = DocumentDAO.getInstance();
    }


  
    /**
     * {@inheritDoc}
     */
    @Override
    public DiscussionsUnreadMessages getDiscussionsUnreadMessages(PortalControllerContext portalControllerContext) throws PortletException {
        // Discussions form
        DiscussionsUnreadMessages unreadMessages = this.applicationContext.getBean(DiscussionsUnreadMessages.class);

        List<CustomTask> discussionsTasks;
        List<Message> messages = new ArrayList<>();
        try {
            discussionsTasks = this.repository.getTasks(portalControllerContext);
            for (CustomTask task: discussionsTasks) {
                Document document = (Document) task.getInnerDocument();
                
                String url = DiscussionHelper.getDiscussionUrlById(portalControllerContext, document.getProperties().getString("ttc:webid"));
                messages.add(new Message( task, url));
            }
        } catch (PortalException e) {
            throw new PortletException(e);
        }
        
     // Sorting
        Collections.sort(messages, new Comparator<Message>() {
                public int compare(Message m1, Message m2)
                {
                    return - ((Document) m1.getTask().getInnerDocument()).getDate("dc:modified").compareTo(((Document) m2.getTask().getInnerDocument()).getDate("dc:modified"));
                }
            });
        
        unreadMessages.setItems(messages);
        
        
 
        return unreadMessages;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ApplicationContextProvider.setApplicationContext(applicationContext);
    }

}
