package fr.index.cloud.ens.ws.beans;

import java.util.Map;

/**
 * Drive.createUser input informations
 * 
 * @author Jean-Sébastien
 */
public class CreateUserBean {
    
    private String firstName;
    private String lastName;
    private String mail;
    
    /**
     * Getter for firstName.
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Setter for firstName.
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Getter for lastName.
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Setter for lastName.
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Getter for mail.
     * @return the mail
     */
    public String getMail() {
        return mail;
    }
    
    /**
     * Setter for mail.
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
      
    public CreateUserBean()   {
        super();
    }

}
