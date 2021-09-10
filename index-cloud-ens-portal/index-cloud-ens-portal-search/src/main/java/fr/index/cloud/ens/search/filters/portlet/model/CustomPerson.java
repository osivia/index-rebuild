package fr.index.cloud.ens.search.filters.portlet.model;

import org.osivia.portal.api.directory.v2.model.Person;

public class CustomPerson {
    
    /** The id. */
    private String id;
 

   
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomPerson other = (CustomPerson) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    /**
     * Setter for id.
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }


    /** The display name. */
    private String displayName;
    


    
    /**
     * Instantiates a new custom person.
     *
     * @param id the id
     */
    public CustomPerson(Person person) {
        super();
        this.id = person.getUid();
        this.displayName = person.getDisplayName();

    }


    /**
     * Getter for displayName.
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    
    /**
     * Setter for displayName.
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    
    /**
     * Getter for id.
     * @return the id
     */
    public String getId() {
        return id;
    }


}
