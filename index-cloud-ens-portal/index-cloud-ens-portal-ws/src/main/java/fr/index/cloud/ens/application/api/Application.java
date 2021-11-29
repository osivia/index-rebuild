package fr.index.cloud.ens.application.api;


public class Application {
    
    
    /** The code. */
    String code;
    
    /** The title. */
    String title;
    
    /** The description. */
    String description;
    
    
    /**
     * Getter for description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    
    /**
     * Setter for description.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for code.
     * @return the code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Setter for code.
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * Getter for title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Setter for title.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
   
    public Application(String code, String title) {
        super();
        this.code = code;
        this.title = title;
    }
    
}
