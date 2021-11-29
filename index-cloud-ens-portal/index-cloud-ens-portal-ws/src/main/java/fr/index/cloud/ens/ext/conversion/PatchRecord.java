package fr.index.cloud.ens.ext.conversion;

import java.util.List;

import fr.index.cloud.ens.ws.beans.MetadataClassifier;

/**
 * The Class PatchRecord.
 */
public class PatchRecord {
    
    /** The field. */
    String docId;
 

    /** The field. */
    String field;
    
    /** The etablissement. */
    String etablissement;
    
    /** The publish meta data. */
    MetadataClassifier publishMetaData;
    
    
    /**
     * Getter for publishMetaData.
     * @return the publishMetaData
     */
    public MetadataClassifier getPublishMetaData() {
        return publishMetaData;
    }

    
    /**
     * Setter for publishMetaData.
     * @param publishMetaData the publishMetaData to set
     */
    public void setPublishMetaData(MetadataClassifier publishMetaData) {
        this.publishMetaData = publishMetaData;
    }


    /** The result code. */
    String resultCode;
    
    /**
     * Getter for field.
     * @return the field
     */
    public String getField() {
        return field;
    }
    
    /**
     * Setter for field.
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }
    
    /**
     * Getter for etablissement.
     * @return the etablissement
     */
    public String getEtablissement() {
        return etablissement;
    }
    
    /**
     * Setter for etablissement.
     * @param etablissement the etablissement to set
     */
    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }
    

    
    /**
     * Getter for resultCode.
     * @return the resultCode
     */
    public String getResultCode() {
        return resultCode;
    }
    
    /**
     * Setter for resultCode.
     * @param resultCode the resultCode to set
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    
    /**
     * Getter for docId.
     * @return the docId
     */
    public String getDocId() {
        return docId;
    }

    
    /**
     * Setter for docId.
     * @param docId the docId to set
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }
}
