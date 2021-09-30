package fr.index.cloud.ens.portal.discussion.portlet.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


public class DiscussionCreation {

    private String type="";
    private String target=""; 
    private String message="";
    private List<String> participants= new ArrayList<String>();
    
    
    /**
     * Getter for participants.
     * @return the participants
     */
    public List<String> getParticipants() {
        return participants;
    }

    
    /**
     * Setter for participants.
     * @param participants the participants to set
     */
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    /**
     * Getter for type.
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Setter for type.
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    
    
    /**
     * Getter for target.
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    
    /**
     * Setter for target.
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Getter for message.
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Setter for message.
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    
    
}
