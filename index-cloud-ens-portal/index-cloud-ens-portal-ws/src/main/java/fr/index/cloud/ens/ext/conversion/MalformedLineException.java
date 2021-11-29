package fr.index.cloud.ens.ext.conversion;


/**
 * The Class MalformedLineException.
 */
public class MalformedLineException extends Exception {

    private final int line;

    private static final long serialVersionUID = -626698508228499854L;

    public MalformedLineException(int line) {
        super( );        
        this.line = line;
    }

    
    /**
     * Getter for line.
     * @return the line
     */
    public int getLine() {
        return line;
    }

 
}
