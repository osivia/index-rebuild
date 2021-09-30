package fr.index.cloud.ens.portal.discussion.portlet.model;

import java.util.Date;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The Class DiscussionMessage.
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class DiscussionMessage {
    
    /** The id. */
    private String id;
    
    /** The author. */
    private String author;
    
    /** The content. */
    private String content;
    
    /** The date. */
    private Date date;
    
    /** The deleted. */
    private boolean deleted;
    
    /** The removal date. */
    private Date removalDate;
       

    /**
     * Getter for author.
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    
    /**
     * Setter for author.
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    
    /**
     * Getter for content.
     * @return the content
     */
    public String getContent() {
        return content;
    }

    
    /**
     * Setter for content.
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    
    /**
     * Getter for date.
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    
    /**
     * Setter for date.
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    
    /**
     * Getter for deleted.
     * @return the deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    
    /**
     * Setter for deleted.
     * @param deleted the deleted to set
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
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
     * Getter for removalDate.
     * @return the removalDate
     */
    public Date getRemovalDate() {
        return removalDate;
    }


    
    /**
     * Setter for removalDate.
     * @param removalDate the removalDate to set
     */
    public void setRemovalDate(Date removalDate) {
        this.removalDate = removalDate;
    }

}
