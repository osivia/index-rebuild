package fr.index.cloud.ens.ws.beans;

import java.util.Map;

/**
 * Drive.createFolder input informations
 * 
 * @author Jean-Sébastien
 */
public class CreateFolderBean {
    private String parentId;
    private String folderName;
    
    /**
     * Getter for parentId.
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }
    
    /**
     * Setter for parentId.
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    /**
     * Getter for folderName.
     * @return the folderName
     */
    public String getFolderName() {
        return folderName;
    }
    
    /**
     * Setter for folderName.
     * @param folderName the folderName to set
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }    

    
  
}
