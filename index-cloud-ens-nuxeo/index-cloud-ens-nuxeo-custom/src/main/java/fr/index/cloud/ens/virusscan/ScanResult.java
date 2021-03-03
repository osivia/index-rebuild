package fr.index.cloud.ens.virusscan;


/**
 * @author Jean-Sébastien
 */
public class ScanResult {
    
    public static final int ERROR_CLEAN = 0;
    public static final int ERROR_VIRUS_FOUND = 1;
    public static final int ERROR_CANNOT_CHECK = 2;

    
    private final int errorCode;
    
    /** The document modified indocator */
    private final boolean modified;
    
    /**
     * Getter for errorCode.
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }
    
    /**
     * Getter for toSave.
     * @return the toSave
     */
    public boolean isModified() {
        return modified;
    }

    public ScanResult(int errorCode, boolean modified) {
        super();
        this.errorCode = errorCode;
        this.modified = modified;
    }
    

}
