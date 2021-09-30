package fr.index.cloud.ens.portal.discussion.portlet.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletException;

import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.tasks.CustomTask;

import fr.index.cloud.ens.portal.discussion.portlet.model.DetailForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;
import fr.index.cloud.ens.portal.discussion.portlet.model.PublicationInfos;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;

/**
 * Discussion portlet repository interface.
 * 
 * @author Jean-sébastien Steux
 */
public interface DiscussionRepository {

    /** model identifier. */
    public static final String MODEL_ID = IFormsService.FORMS_WEB_ID_PREFIX + "discussion";
    
    
    /**
     * Get discussion.
     * 
     * @param portalControllerContext portal controller context
     * @return discussions documents
     * @throws PortalException 
     * @throws PortletException
     */

    List<DiscussionDocument> getDiscussions(PortalControllerContext portalControllerContext) throws PortletException;




     /**
      * Mark as deleted.
      *
      * @param portalControllerContext the portal controller context
      * @param documents the documents
      * @throws PortletException the portlet exception
      */
    void markAsDeleted(PortalControllerContext portalControllerContext, List<DiscussionDocument> documents) throws PortletException ;


    /**
     * Create new Discussion
     *
     * @param portalControllerContext portal controller context
     * @param discution create bean
     * @return 
     */
    Document createDiscussion(PortalControllerContext portalControllerContext, DiscussionCreation discution) throws PortletException;



    /**
     * Adds the message.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void addMessage(PortalControllerContext portalControllerContext, DetailForm form) throws PortletException;


    /**
     * Gets the discussion.
     *
     * @param portalControllerContext the portal controller context
     * @param id the id
     * @return the discussion
     * @throws PortletException the portlet exception
     */
    DiscussionDocument getDiscussion(PortalControllerContext portalControllerContext, DetailForm form) throws PortletException;


    /**
     * Delete message.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @param messageId the message id
     * @throws PortletException the portlet exception
     */
    void deleteMessage(PortalControllerContext portalControllerContext, DetailForm form, String messageId) throws PortletException;


    /**
     * Check user pref.
     *
     * @param portalControllerContext the portal controller context
     * @param id the document id
     * @param lastMessageId the last message id 
     */
    void checkUserReadPreference(PortalControllerContext portalControllerContext, String id, int lastMessageId) throws PortletException;




    /**
     * Gets the discussion by participant.
     *
     * @param portalControllerContext the portal controller context
     * @param participant the participant
     * @param joinConversation the join conversation
     * @return the discussion by participant
     * @throws PortletException the portlet exception
     */
    DiscussionDocument getDiscussionByParticipant(PortalControllerContext portalControllerContext, String participant, String publicationId,
            boolean joinConversation) throws PortletException;




    /**
     * Gets the tasks.
     *
     * @param portalControllerContext the portal controller context
     * @return the tasks
     * @throws PortletException the portlet exception
     * @throws PortalException 
     */
    List<CustomTask> getTasks(PortalControllerContext portalControllerContext) throws PortalException, PortletException;






    /**
     * Gets the discussion by publication.
     *
     * @param portalControllerContext the portal controller context
     * @param publicationId the publication id
     * @param joinConversation the join conversation
     * @return the discussion by publication
     * @throws PortletException the portlet exception
     */
    DiscussionDocument getDiscussionByPublication(PortalControllerContext portalControllerContext, String publicationId, boolean joinConversation)
            throws PortletException;






    /**
     * Gets the discussion linked to a publication.
     *
     * @param portalControllerContext the portal controller context
     * @param webId the web id
     * @return the discussion by publication
     * @throws PortalException the portal exception
     */
    Map<String, PublicationInfos> getDiscussionPubInfosByPublication(PortalControllerContext portalControllerContext, String webId) throws PortalException;


    /**
     * Gets the local publication discussions for the current user
     *
     * @param portalControllerContext the portal controller context
     * @return the local publication discussions web id
     * @throws PortalException the portal exception
     */

    Map<String, PublicationInfos> getDiscussionsPubInfosByCopy(PortalControllerContext portalControllerContext) throws PortalException;





















 
}
