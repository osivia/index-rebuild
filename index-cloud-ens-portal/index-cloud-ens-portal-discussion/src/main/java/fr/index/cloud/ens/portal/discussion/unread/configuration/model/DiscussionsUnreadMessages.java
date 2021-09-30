package fr.index.cloud.ens.portal.discussion.unread.configuration.model;

import java.util.List;

import org.osivia.portal.api.portlet.Refreshable;
import org.osivia.portal.api.portlet.RequestLifeCycle;
import org.osivia.portal.api.tasks.CustomTask;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;

/**
 * Discussions form java-bean.
 * 
 * @author Jean Sébastien Steux
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Refreshable
@RequestLifeCycle
public class DiscussionsUnreadMessages {

    /** Discussions documents. */
    private List<Message> items;



    
    /**
     * Getter for tasks.
     * @return the tasks
     */
    public List<Message> getItems() {
        return items;
    }



    
    /**
     * Setter for tasks.
     * @param tasks the tasks to set
     */
    public void setItems(List<Message> items) {
        this.items = items;
    }



    /**
     * Constructor.
     */
    public DiscussionsUnreadMessages() {
        super();
    }


   
}
