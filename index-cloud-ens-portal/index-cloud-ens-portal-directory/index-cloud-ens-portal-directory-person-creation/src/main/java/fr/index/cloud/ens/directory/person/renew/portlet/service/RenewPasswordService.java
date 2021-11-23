/**
 * 
 */
package fr.index.cloud.ens.directory.person.renew.portlet.service;

import org.dom4j.Element;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.validation.Errors;

import fr.index.cloud.ens.directory.person.renew.portlet.controller.RenewPasswordForm;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;

/**
 * @author Loïc Billon
 *
 */
public interface RenewPasswordService {
	

    /** model identifier. */
    public static final String MODEL_ID = IFormsService.FORMS_WEB_ID_PREFIX + "renew-password";


	/**
	 * @param portalControllerContext
	 * @param form
	 */
	void proceedInit(PortalControllerContext portalControllerContext, RenewPasswordForm form);

	/**
	 * @param portalControllerContext
	 * @param form
	 */
	void proceedPassword(PortalControllerContext portalControllerContext, RenewPasswordForm form);

    /**
     * Validate password rules.
     *
     * @param errors   validation errors
     * @param field    password field name
     * @param password password value
     */
    void validatePasswordRules(Errors errors, String field, String password);    	
	
	/**
	 * @param portalControllerContext
	 * @param password
	 * @return
	 */
	Element getPasswordRulesInformation(PortalControllerContext portalControllerContext, String password);

}
