package fr.index.cloud.ens.portal.discussion.portlet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Discussion document java-bean.
 * 
 * @author Jean-Sébastien Steux
 */
public class DiscussionDocument {
    
    public static final String TYPE_USER_COPY = "USER_COPY";


    /** The id. */
    private String id;

    /** The path. */
    private String path;

    /** The title. */
    private String title;

    /** The last modified. */
    private Date lastModified;

    /** The last contributor. */
    private String lastContributor;
    
    /** The last message. */
    private String lastMessageExtract;    




    /** The web id. */
    private String webId;
    

    /** The target. */
    private final String target;
    
    /** The type. */
    private final String type;
    

    /** The participants. */
    private List<String> participants;

    /** The document. */
    private Document document;

    /** The publication. */
    private PublicationInfos publication;



    /** The messages. */
    private List<DiscussionMessage> messages;
    
    /** The mark as deleted. */
    private boolean markAsDeleted = false;

    
    
 
    /**
     * Gets the deleted key.
     *
     * @param id the id
     * @return the deleted key
     */
    public static String getDeletedKey( String id)   {
        return "discussion." + id + ".deleted.messageId";
    }

    
    /**
     * Getter for markAsDeleted.
     * @return the markAsDeleted
     */
    public boolean isMarkAsDeleted() {
        return markAsDeleted;
    }

    
    /**
     * Getter for publication.
     * @return the publication
     */
    public PublicationInfos getPublication() {
        return publication;
    }


    
    /**
     * Setter for publication.
     * @param publication the publication to set
     */
    public void setPublication(PublicationInfos publication) {
        this.publication = publication;
    }

    /**
     * Getter for messages.
     * 
     * @return the messages
     */
    public List<DiscussionMessage> getMessages() {

        if (messages == null) {

            messages = new ArrayList<DiscussionMessage>();

            PropertyList messagesProp = document.getProperties().getList("disc:messages");
            PropertyList removedMessagesProp = document.getProperties().getList("disc:removedMessages");


            for (int i = 0; i < messagesProp.size(); i++) {

                PropertyMap messageProp = messagesProp.getMap(i);

                DiscussionMessage message = new DiscussionMessage();
                String content = messageProp.getString("content");
                message.setContent(content.replaceAll("\\n", "<br>"));
                message.setDate(messageProp.getDate("date"));
                message.setAuthor(messageProp.getString("author"));
                message.setId(Integer.toString(i));


                // Removed messages

                for (int j = 0; j < removedMessagesProp.size(); j++) {
                    PropertyMap removedMessageProp = removedMessagesProp.getMap(j);
                    String removedId = removedMessageProp.getString("messageId");

                    if (StringUtils.equalsIgnoreCase(message.getId(), removedId)) {
                        message.setDeleted(true);
                        message.setRemovalDate(removedMessageProp.getDate("date"));

                        break;
                    }
                }

                messages.add(message);

            }

        }
        return messages;
    }


    /**
     * Constructor.
     * 
     * @param discussion document DTO
     */
    public DiscussionDocument(PortalControllerContext portalControllerContext, PersonService personService, IBundleFactory bundleFactory , Map<String, String> userProperties, String currentUser, Document discussion, PublicationInfos publicationInfos) {

        id = discussion.getId();
        path = discussion.getPath();

        webId = discussion.getString("ttc:webid");
        PropertyList participantsProp = discussion.getProperties().getList("disc:participants");
        participants = new ArrayList<>();
        for (Object name : participantsProp.list()) {
            participants.add((String) name);
        }
        
        target = discussion.getProperties().getString("disc:target");
        type = discussion.getProperties().getString("disc:type");
        this.title = getTitle(portalControllerContext, personService, bundleFactory, participants, type, publicationInfos);
             
        
        this.document = discussion;
        this.publication = publicationInfos;


        // last message
        for (int iMessage = getMessages().size() - 1; iMessage >= 0 && lastContributor == null; iMessage--) {
            if (!getMessages().get(iMessage).isDeleted()) {
                lastContributor = getMessages().get(iMessage).getAuthor();
                lastModified = getMessages().get(iMessage).getDate();
                lastMessageExtract = getMessages().get(iMessage).getContent();
                int firstLigneEnd = lastMessageExtract.toLowerCase().indexOf("<br>");
                if( firstLigneEnd != -1)
                    lastMessageExtract = lastMessageExtract.substring(0, firstLigneEnd);
            }
        }
        String deletedMessageId = userProperties.get(getDeletedKey(webId));
        if (deletedMessageId != null) {

            int deleteMsgId = Integer.parseInt(deletedMessageId);

            if (getMessages().size() <= deleteMsgId + 1) {
                markAsDeleted = true;
            }
        }


    }



    /**
     * Constructor for new discussion
     * 
     * @param document document DTO
     */
    public DiscussionDocument(PortalControllerContext portalControllerContext, PersonService personService, IBundleFactory bundleFactory, Map<String, String> userProperties, String currentUser, List<String> participants, PublicationInfos publication) {
        this.messages = new ArrayList<DiscussionMessage>();
        this.participants = participants;
        if( publication != null)
            this.target = publication.getTarget();
        else
            this.target = null;
        this.type = null;
        this.title = getTitle(portalControllerContext, personService, bundleFactory,  participants, type, publication);
        this.publication = publication;        
     }

    
    /**
     * Constructor for new discussion
     * 
     * @param document document DTO
     */
    public DiscussionDocument(PortalControllerContext portalControllerContext, PersonService personService, IBundleFactory bundleFactory, Map<String, String> userProperties,  String currentUser, String type,  PublicationInfos publication) {
        this.messages = new ArrayList<DiscussionMessage>();
        
        this.type = type;
        if( publication != null)
            this.target = publication.getTarget();
        else
            this.target = null;
        this.title = getTitle(portalControllerContext, personService, bundleFactory, participants, type, publication);
        this.publication = publication;        

    }

    /**
     * Getter for id.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }


    /**
     * Getter for title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }


    /**
     * Getter for lastModified.
     * 
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }


    /**
     * Getter for lastContributor.
     * 
     * @return the lastContributor
     */
    public String getLastContributor() {
        return lastContributor;
    }


    /**
     * Getter for webId.
     * 
     * @return the webId
     */
    public String getWebId() {
        return webId;
    }


    /**
     * Getter for participant.
     * 
     * @return the participant
     */
    public List<String> getParticipants() {
        return participants;
    }
    
    
    /**
     * Getter for document.
     * @return the document
     */
    public Document getDocument() {
        return document;
    }


    
    /**
     * Getter for target.
     * @return the target
     */
    public String getTarget() {
        return target;
    }


    
    /**
     * Getter for type.
     * @return the type
     */
    public String getType() {
        return type;
    }


    
    /**
     * Getter for lastMessage.
     * @return the lastMessage
     */
    public String getLastMessageExtract() {
        return lastMessageExtract;
    }
    
    

    /**
     * Gets the title.
     *
     * @param portalControllerContext the portal controller context
     * @param discussion the discussion
     * @return the title
     * @throws PortletException the portlet exception
     */
    private String getTitle(PortalControllerContext portalControllerContext, PersonService personService, IBundleFactory bundleFactory , List<String> participants, String type, PublicationInfos publicationInfos) {


            String currentUser = portalControllerContext.getHttpServletRequest().getRemoteUser();

            
            String title=null;

            /* title by publication */
            if ( publicationInfos != null) {
                title = publicationInfos.getTitle();
           }

            /* tile by participant */
            
            if( title == null)  {
               if (participants.size() > 0)
                    title = getTitleByParticipants(personService, currentUser, participants);
            }
            
            
            // Internationalization bundle
            Bundle bundle = bundleFactory.getBundle(portalControllerContext.getHttpServletRequest().getLocale());
            
            // Généric title
            if (title == null)
                title = bundle.getString("DISCUSSION_DISCUSSIONS_TITLE_DETAIL");
            
            // Groupe suffix
            if (StringUtils.equals(type, DiscussionDocument.TYPE_USER_COPY))
                title = title + " (" + bundle.getString("DISCUSSION_GROUP") + ")";
            

            return title;



    }
    
    /**
     * Update title.
     *
     * @param personService the person service
     * @param currentUser the current user
     */
    private String getTitleByParticipants( PersonService personService, String currentUser, List<String> participants) {

        String title = null;

        if (participants != null) {
            // Title
            for (String name : participants) {
                if (!StringUtils.equals(name, currentUser)) {
                    Person person = personService.getPerson(name);
                    if (person != null && person.getDisplayName() != null)
                        title = person.getDisplayName();
                }
            }
        }

        return title;
    }
    
}
