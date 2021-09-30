package fr.index.cloud.ens.portal.discussion.portlet.model;

import java.util.Date;

import org.nuxeo.ecm.automation.client.model.Document;

import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;

public class PublicationInfos {
    
    /** The target document. */
    private final Document targetDocument;
    
    /** The target. */
    private final String target;

    /** The title. */
    private final String title;    
    
    /** The target DTO. */
    private final DocumentDTO targetDTO;
    
    


    /** The last recopy. */
    Date    lastRecopy;
    
    public PublicationInfos(String target, String title, Document targetDocument, DocumentDTO targetDTO) {
        super();
        this.targetDocument = targetDocument;
        this.title = title;
        this.target = target;
        this.targetDTO = targetDTO;
    }


    /**
     * Getter for title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    
    
    /**
     * Getter for target.
     * @return the target
     */
    public String getTarget() {
        return target;
    }


    /**
     * Getter for lastRecopy.
     * @return the lastRecopy
     */
    public Date getLastRecopy() {
        return lastRecopy;
    }
    
    /**
     * Setter for lastRecopy.
     * @param lastRecopy the lastRecopy to set
     */
    public void setLastRecopy(Date lastRecopy) {
        this.lastRecopy = lastRecopy;
    }
    
    /**
     * Getter for targetDocument.
     * @return the targetDocument
     */
    public Document getTargetDocument() {
        return targetDocument;
    }
    
    
    /**
     * Getter for targetDTO.
     * @return the targetDTO
     */
    public DocumentDTO getTargetDTO() {
        return targetDTO;
    }

}
