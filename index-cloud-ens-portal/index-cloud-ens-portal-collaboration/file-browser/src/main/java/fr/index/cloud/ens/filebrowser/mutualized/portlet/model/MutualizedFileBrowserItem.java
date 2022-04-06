package fr.index.cloud.ens.filebrowser.mutualized.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserItem;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Mutualized file browser item java-bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserItem
 */
@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MutualizedFileBrowserItem extends AbstractFileBrowserItem {
    
    /** The views. */
    public Long views;
    
    /** The downloads. */
    public Long downloads;
    
    /** The licence. */
    private String licence;
    
    
    



    /**
     * Getter for views.
     * @return the views
     */
    public Long getViews() {
        return views;
    }

    
    /**
     * Setter for views.
     * @param views the views to set
     */
    public void setViews(Long views) {
        this.views = views;
    }

    
    /**
     * Getter for download.
     * @return the download
     */
    public Long getDownloads() {
        return downloads;
    }

    
    /**
     * Setter for download.
     * @param download the download to set
     */
    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

    /**
     * Gets the licence.
     *
     * @return the licence
     */
    public String getLicence() {
        return licence;
    }


    
    /**
     * Sets the licence.
     *
     * @param licence the new licence
     */
    public void setLicence(String licence) {
        this.licence = licence;
    }
    
    
    /**
     * Constructor.
     */
    public MutualizedFileBrowserItem() {
        super();
    }
    
    

}
