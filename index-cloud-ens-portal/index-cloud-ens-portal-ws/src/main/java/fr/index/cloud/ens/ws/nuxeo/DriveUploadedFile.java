package fr.index.cloud.ens.ws.nuxeo;

import java.io.File;

/**
 * The Class DriveUploadedFile.
 */
public class DriveUploadedFile {
    
    /** The name. */
    private String name;
    
    /** The content type. */
    private String contentType;
    
    /** The file. */
    private File file;
    
    /**
     * Getter for name.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Instantiates a new drive uploaded file.
     *
     * @param name the name
     * @param contentType the content type
     * @param file the file
     */
    public DriveUploadedFile(String name, String contentType, File file) {
        super();
        this.name = name;
        this.contentType = contentType;
        this.file = file;
    }

    /**
     * Getter for contentType.
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Getter for file.
     * @return the file
     */
    public File getFile() {
        return file;
    }

    

}
