package fr.index.cloud.ens.ws.nuxeo;


public class GetFileSystemBody {
    private String id;
    private String parentId;
    
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
}
