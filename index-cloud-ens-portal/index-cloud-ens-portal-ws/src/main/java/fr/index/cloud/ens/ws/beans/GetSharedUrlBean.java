package fr.index.cloud.ens.ws.beans;

import java.util.Map;

/**
 * Drive.getSharedUrl input informations
 * 
 * @author Jean-Sébastien
 */
public class GetSharedUrlBean {
    private String contentId;
    private String format;    
    private boolean publish;
 

    
   


    /**
     * Getter for format.
     * @return the format
     */
    public String getFormat() {
		return format;
	}


	/**
     * Setter for format.
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    
    /**
     * Getter for contentId.
     * @return the contentId
     */
    public String getContentId() {
        return contentId;
    }
    
    /**
     * Setter for contentId.
     * @param contentId the contentId to set
     */
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
    

    /**
     * Getter for publish.
     * @return the publish
     */
    public boolean isPublish() {
        return publish;
    }

    
    /**
     * Setter for publish.
     * @param publish the publish to set
     */
    public void setPublish(boolean publish) {
        this.publish = publish;
    }
    
}
