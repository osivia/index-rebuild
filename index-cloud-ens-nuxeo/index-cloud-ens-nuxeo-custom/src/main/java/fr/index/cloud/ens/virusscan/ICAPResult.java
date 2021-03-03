package fr.index.cloud.ens.virusscan;

/**
 * The Class ICAPResult.
 */
public class ICAPResult {


    /** The state. */
    private final int state;
    
    /** The state. */
    private final String virusName;


    /** The Constant STATE_CHECKED. */
    public final static int STATE_CHECKED = 0;
    
    /** The Constant STATE_VIRUS_FOUND. */
    public final static int STATE_VIRUS_FOUND = 1;


    /**
     * Instantiates a new ICAP result.
     *
     * @param state the state
     */
    public ICAPResult(int state, String virusName) {
        super();
        this.state = state;
        this.virusName = virusName;
    }

    

    /**
     * Instantiates a new ICAP result.
     *
     * @param state the state
     */
    public ICAPResult(int state) {
        super();
        this.state = state;
        this.virusName = null;
    }


    /**
     * Getter for STATE_PROCESSING.
     * 
     * @return the stateProcessing
     */
    public int getStateProcessing() {
        return state;
    }

    /**
     * Getter for virusName.
     * @return the virusName
     */
    public String getVirusName() {
        return virusName;
    }

}
