package fr.index.cloud.ens.ws.beans;

import java.util.Set;
import java.util.TreeSet;

/**
 * User datas for storage service
 */

public class UserStorageBean {
    
    String userId="";

    Set<String> clientId=new TreeSet<String>();
    long fileSize = 0;
    long quota = 0;    
    
 
   
    /**
     * Getter for quota.
     * @return the quota
     */
    public long getQuota() {
        return quota;
    }


    
    /**
     * Setter for quota.
     * @param quota the quota to set
     */
    public void setQuota(long quota) {
        this.quota = quota;
    }



    /**
     * Getter for fileSize.
     * @return the fileSize
     */
    public long getFileSize() {
        return fileSize;
    }



    
    /**
     * Setter for fileSize.
     * @param fileSize the fileSize to set
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }


    /**
     * Getter for clientId.
     * @return the clientId
     */
    public Set<String> getClientId() {
        return clientId;
    }



    /**
     * Getter for userId.
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    
    /**
     * Setter for userId.
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    

}
