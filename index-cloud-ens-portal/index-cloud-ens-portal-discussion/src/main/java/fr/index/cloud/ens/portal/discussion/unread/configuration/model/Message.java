package fr.index.cloud.ens.portal.discussion.unread.configuration.model;

import org.osivia.portal.api.tasks.CustomTask;

/**
 * The Class Message.
 */

public class Message {

    /** The task. */
    private final CustomTask task;

    /** The url. */
    private final String url;


    
    



    
    /**
     * Getter for url.
     * @return the url
     */
    public String getUrl() {
        return url;
    }


    public Message(CustomTask task, String url) {
        super();
        this.url = url;
        this.task = task;

    }


    /**
     * Getter for task.
     * 
     * @return the task
     */
    public CustomTask getTask() {
        return task;
    }

    

}
