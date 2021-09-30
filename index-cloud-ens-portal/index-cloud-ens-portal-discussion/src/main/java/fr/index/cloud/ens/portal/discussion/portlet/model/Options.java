package fr.index.cloud.ens.portal.discussion.portlet.model;

import java.util.List;

import org.osivia.portal.api.portlet.Refreshable;
import org.osivia.portal.api.portlet.RequestLifeCycle;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Discussion detail form java-bean.
 * 
 * @author Jean-sébastien steux
 */

public class Options {


    public static String MODE_ADMIN = "admin";

    /**
     * Constructor.
     */
    public Options() {
        super();
    }


    /** mode indicator. */
    private String mode;

    /** The administrator. */
    private boolean administrator;
    
    /** The id. */
    private String id;
    
    /** The participant. */
    private String participant;
    
    /** The publication id. */
    private String publicationId;
    
    /** The message id. */
    private String  messageId;    

  
    
    /**
     * Getter for id.
     * @return the id
     */
    public String getId() {
        return id;
    }


    
    /**
     * Setter for id.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }


    
    /**
     * Getter for participant.
     * @return the participant
     */
    public String getParticipant() {
        return participant;
    }


    
    /**
     * Setter for participant.
     * @param participant the participant to set
     */
    public void setParticipant(String participant) {
        this.participant = participant;
    }


    
    /**
     * Getter for publicationId.
     * @return the publicationId
     */
    public String getPublicationId() {
        return publicationId;
    }


    
    /**
     * Setter for publicationId.
     * @param publicationId the publicationId to set
     */
    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }


    /**
     * Getter for messageId.
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    
    /**
     * Setter for messageId.
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Getter for administrator.
     * 
     * @return the administrator
     */
    public boolean isAdministrator() {
        return administrator;
    }

    /**
     * Setter for administrator.
     * 
     * @param administrator the administrator to set
     */
    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }


    /**
     * Getter for mode.
     * 
     * @return the mode
     */
    public String getMode() {
        return mode;
    }


    /**
     * Setter for mode.
     * 
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }


}
