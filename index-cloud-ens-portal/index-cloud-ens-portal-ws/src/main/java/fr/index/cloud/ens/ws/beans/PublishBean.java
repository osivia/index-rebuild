package fr.index.cloud.ens.ws.beans;

import java.util.List;
import java.util.Map;

/**
 * Drive.publish input informations
 * 
 * @author Jean-Sébastien
 */
public class PublishBean {
    
    /** The share url. */
    private String shareUrl;
    
 
    
    /** The pub group. */
    private List<String> pubGroups;    
    
    
    /**
     * Getter for pubGroups.
     * @return the pubGroups
     */
    public List<String> getPubGroups() {
        return pubGroups;
    }



    
    /**
     * Setter for pubGroups.
     * @param pubGroups the pubGroups to set
     */
    public void setPubGroups(List<String> pubGroups) {
        this.pubGroups = pubGroups;
    }


    /** The pub context. */
    private String pubContext;    
    
    /** The pub school yeard. */
    private String schoolYear;        
    



    
    /**
     * Getter for schoolYear.
     * @return the schoolYear
     */
    public String getSchoolYear() {
        return schoolYear;
    }




    
    /**
     * Setter for schoolYear.
     * @param schoolYear the schoolYear to set
     */
    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }




    /**
     * Gets the share url.
     *
     * @return the share url
     */
    public String getShareUrl() {
		return shareUrl;
	}



	/**
	 * Sets the share url.
	 *
	 * @param shareUrl the new share url
	 */
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}



    
    /**
     * Getter for pubContext.
     * @return the pubContext
     */
    public String getPubContext() {
        return pubContext;
    }

   
    /**
     * Setter for pubContext.
     * @param pubContext the pubContext to set
     */
    public void setPubContext(String pubContext) {
        this.pubContext = pubContext;
    }

    
    


    /** The properties. */
    // 
    DocumentProperties properties;


    
    /**
     * Getter for properties.
     * @return the properties
     */
    public DocumentProperties getProperties() {
        return properties;
    }


    
    /**
     * Setter for properties.
     * @param properties the properties to set
     */
    public void setProperties(DocumentProperties properties) {
        this.properties = properties;
    }
    
   

}
