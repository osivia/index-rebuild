/**
 * 
 */
package fr.index.cloud.ens.directory.person.renew.portlet.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Loïc Billon
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RenewPasswordForm {

	public enum RenewPasswordStep { INIT, SEND, PASSWORD, CONFIRM };
    /** Default view. */
    public static final RenewPasswordStep DEFAULT = RenewPasswordStep.INIT;
    
	private String mail;
	
    private String newpassword;
    
    private String password2;  


	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

    /**
	 * @return the newpassword
	 */
	public String getNewpassword() {
		return newpassword;
	}

	/**
	 * @param newpassword the newpassword to set
	 */
	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	/**
     * Getter for password2.
     * @return the password2
     */
    public String getPassword2() {
        return password2;
    }

    
    /**
     * Setter for password2.
     * @param password2 the password2 to set
     */
    public void setPassword2(String password2) {
        this.password2 = password2;
    }

	
}
