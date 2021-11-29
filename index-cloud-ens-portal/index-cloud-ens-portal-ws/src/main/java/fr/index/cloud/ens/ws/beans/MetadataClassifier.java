package fr.index.cloud.ens.ws.beans;

import java.util.List;

public class MetadataClassifier {
    
    private List<String> codes;
    private String name;
    
    /**
     * Getter for codes.
     * @return the codes
     */
    public List<String> getCodes() {
        return codes;
    }
    
    /**
     * Setter for codes.
     * @param codes the codes to set
     */
    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
    
    /**
     * Getter for name.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setter for name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    

}
