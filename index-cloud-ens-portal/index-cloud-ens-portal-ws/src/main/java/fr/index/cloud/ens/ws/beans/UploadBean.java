package fr.index.cloud.ens.ws.beans;

/**
 * Drive.upload input informations
 * 
 * @author Jean-Sébastien
 */
public class UploadBean {
    
    /** The parent id. */
    private String parentId;
    
    /** The parent fullname. */
    private String parentPath;

    
    
    /** The action if exists. */
    private String ifFileExists;
    


    /** The action overwrite. */
    public static String ACTION_OVERWRITE = "overwrite";
    
    /** The action ignore. */
    public static String ACTION_IGNORE = "ignore";
    
    /** The action rename. */
    public static String ACTION_RENAME = "rename";   
    



    
    /**
     * Getter for parentPath.
     * @return the parentPath
     */
    public String getParentPath() {
        return parentPath;
    }


 
    /**
     * Setter for parentPath.
     * @param parentPath the parentPath to set
     */
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }



    /** The properties. */
    DocumentProperties properties;

     
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
    
    
    
    /**
     * Getter for onFileExists.
     * @return the onFileExists
     */
    public String getIfFileExists() {
        return ifFileExists;
    }


    
    /**
     * Setter for onFileExists.
     * @param onFileExists the onFileExists to set
     */
    public void setIfFileExists(String onFileExists) {
        this.ifFileExists = onFileExists;
    }




}
