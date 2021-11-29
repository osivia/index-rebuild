package fr.index.cloud.ens.conversion.admin;

import java.io.File;

import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * Application edition form java-bean.
 *
 * @author Jean-Sébastien Steux
 */
@Component
@Refreshable
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class ConversionAdminForm {

    /** original URL. */
    private String fileDownloadUrl;
    

    /** upload multipart file. */
    private MultipartFile fileUpload;
    
    /** The error message. */
    private String errorMessage;
    
    /** The error message. */
    private String successMessage;
    


    /** upload multipart file. */
    private MultipartFile patchUpload;
    
    /** The error message. */
    private String patchErrorMessage;
    
    /** The error message. */
    private String patchSuccessMessage;



    private File temporaryFile;
    private String fileName;
    private String contentType;

 

    private File patchTemporaryFile;
    private String patchFileName;
    private String pathContentType;



    public ConversionAdminForm() {
        super();

    }


    /**
     * Getter for fileDownloadUrl.
     * @return the fileDownloadUrl
     */
    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }


    
    /**
     * Setter for fileDownloadUrl.
     * @param fileDownloadUrl the fileDownloadUrl to set
     */
    public void setFileDownloadUrl(String fileDownloadUrl) {
        this.fileDownloadUrl = fileDownloadUrl;
    }


    
    /**
     * Getter for fileUpload.
     * @return the fileUpload
     */
    public MultipartFile getFileUpload() {
        return fileUpload;
    }


    
    /**
     * Setter for fileUpload.
     * @param fileUpload the fileUpload to set
     */
    public void setFileUpload(MultipartFile fileUpload) {
        this.fileUpload = fileUpload;
    }

    

    
    /**
     * Getter for errorMessage.
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }


    
    /**
     * Setter for errorMessage.
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    

    
    /**
     * Getter for successMessage.
     * @return the successMessage
     */
    public String getSuccessMessage() {
        return successMessage;
    }


    
    /**
     * Setter for successMessage.
     * @param successMessage the successMessage to set
     */
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
    
    /**
     * Getter for temporaryFile.
     * @return the temporaryFile
     */
    public File getTemporaryFile() {
        return temporaryFile;
    }


    
    /**
     * Setter for temporaryFile.
     * @param temporaryFile the temporaryFile to set
     */
    public void setTemporaryFile(File temporaryFile) {
        this.temporaryFile = temporaryFile;
    }


    
    /**
     * Getter for fileName.
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }


    
    /**
     * Setter for fileName.
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    
    /**
     * Getter for contentType.
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }


    
    /**
     * Setter for contentType.
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    
    /**
     * Getter for patchUpload.
     * @return the patchUpload
     */
    public MultipartFile getPatchUpload() {
        return patchUpload;
    }


    
    /**
     * Setter for patchUpload.
     * @param patchUpload the patchUpload to set
     */
    public void setPatchUpload(MultipartFile patchUpload) {
        this.patchUpload = patchUpload;
    }


    
    /**
     * Getter for patchErrorMessage.
     * @return the patchErrorMessage
     */
    public String getPatchErrorMessage() {
        return patchErrorMessage;
    }


    
    /**
     * Setter for patchErrorMessage.
     * @param patchErrorMessage the patchErrorMessage to set
     */
    public void setPatchErrorMessage(String patchErrorMessage) {
        this.patchErrorMessage = patchErrorMessage;
    }


    
    /**
     * Getter for patchSuccessMessage.
     * @return the patchSuccessMessage
     */
    public String getPatchSuccessMessage() {
        return patchSuccessMessage;
    }


    
    /**
     * Setter for patchSuccessMessage.
     * @param patchSuccessMessage the patchSuccessMessage to set
     */
    public void setPatchSuccessMessage(String patchSuccessMessage) {
        this.patchSuccessMessage = patchSuccessMessage;
    }


    /**
     * Getter for patchTemporaryFile.
     * @return the patchTemporaryFile
     */
    public File getPatchTemporaryFile() {
        return patchTemporaryFile;
    }


    
    /**
     * Setter for patchTemporaryFile.
     * @param patchTemporaryFile the patchTemporaryFile to set
     */
    public void setPatchTemporaryFile(File patchTemporaryFile) {
        this.patchTemporaryFile = patchTemporaryFile;
    }


    
    /**
     * Getter for patchFileName.
     * @return the patchFileName
     */
    public String getPatchFileName() {
        return patchFileName;
    }


    
    /**
     * Setter for patchFileName.
     * @param patchFileName the patchFileName to set
     */
    public void setPatchFileName(String patchFileName) {
        this.patchFileName = patchFileName;
    }


    
    /**
     * Getter for pathContentType.
     * @return the pathContentType
     */
    public String getPatchContentType() {
        return pathContentType;
    }


    
    /**
     * Setter for pathContentType.
     * @param pathContentType the pathContentType to set
     */
    public void setPatchContentType(String pathContentType) {
        this.pathContentType = pathContentType;
    }

    
    /**
     * Getter for logs.
     * @return the logs
     */
    public String getLogs() {
        return logs;
    }


    
    /**
     * Setter for logs.
     * @param logs the logs to set
     */
    public void setLogs(String logs) {
        this.logs = logs;
    }


    
    /**
     * Getter for nbLines.
     * @return the nbLines
     */
    public int getNbLines() {
        return nbLines;
    }


    
    /**
     * Setter for nbLines.
     * @param nbLines the nbLines to set
     */
    public void setNbLines(int nbLines) {
        this.nbLines = nbLines;
    }



    /** The logs. */
    private String logs;
    
    /** The nb lines. */
    private int nbLines=100;
    
}
