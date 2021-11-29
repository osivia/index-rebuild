package fr.index.cloud.ens.ws.beans;

import java.util.List;

public class DocumentProperties {
    
    List<MetadataClassifier> levels;
    
    MetadataClassifier subject;
    
    List<String> keywords;

    String documentType;
    
    
    
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


    /**
     * Getter for levels.
     * @return the levels
     */
    public List<MetadataClassifier> getLevels() {
        return levels;
    }

    
    /**
     * Setter for levels.
     * @param levels the levels to set
     */
    public void setLevels(List<MetadataClassifier> levels) {
        this.levels = levels;
    }

    
    /**
     * Getter for subject.
     * @return the subject
     */
    public MetadataClassifier getSubject() {
        return subject;
    }

    
    /**
     * Setter for subject.
     * @param subject the subject to set
     */
    public void setSubject(MetadataClassifier subject) {
        this.subject = subject;
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

}
