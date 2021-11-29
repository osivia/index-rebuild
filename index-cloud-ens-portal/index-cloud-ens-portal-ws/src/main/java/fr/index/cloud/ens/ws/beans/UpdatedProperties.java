package fr.index.cloud.ens.ws.beans;

import java.util.List;

/**
 * The document properties to save
 */


public class UpdatedProperties {
    
    /** The levels. */
    List<String> levels;
    
    /** The subject. */
    String subject;
    
    /** The keywords. */
    List<String> keywords;

    /** The document type. */
    String documentType;
    
    /**
     * Getter for subject.
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }


    
    /**
     * Setter for subject.
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }




    
    /**
     * Getter for levels.
     * @return the levels
     */
    public List<String> getLevels() {
        return levels;
    }

    
    /**
     * Setter for levels.
     * @param levels the levels to set
     */
    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

    

    
    /**
     * Getter for keywords.
     * @return the keywords
     */
    public List<String> getKeywords() {
        return keywords;
    }

    
    /**
     * Setter for keywords.
     * @param keywords the keywords to set
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    
    /**
     * Getter for documentType.
     * @return the documentType
     */
    public String getDocumentType() {
        return documentType;
    }

    
    /**
     * Setter for documentType.
     * @param documentType the documentType to set
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

}
