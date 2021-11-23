/**
 *
 */
package fr.index.cloud.ens.directory.person.creation.portlet.service;

import fr.index.cloud.ens.directory.person.creation.portlet.model.PersonCreationForm;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;
import org.dom4j.Element;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.validation.Errors;

import javax.portlet.PortletException;

/**
 * Person creation portlet service interface.
 *
 * @author Loïc Billon
 * @author Cédric Krommenhoek
 */
public interface PersonCreationService {

    /** Model identifier. */
    String MODEL_ID = IFormsService.FORMS_WEB_ID_PREFIX + "person-creation-pronote";

    /** Person identifier length. */
    int PERSON_UID_LENGTH = 10;


    /**
     * Get password rules informations.
     *
     * @param portalControllerContext portal controller context
     * @param newpassword password
     * @return password rules informations.
     */
    Element getPasswordRulesInformation(PortalControllerContext portalControllerContext, String newpassword);


    /**
     * Validate password rules.
     * @param errors form errors
     * @param field password field
     * @param password password value
     */
    void validatePasswordRules(Errors errors, String field, String password);


    /**
     * Proceed person creation initialization.
     * @param portalControllerContext portal controller context
     * @param form form
     */
    void proceedInit(PortalControllerContext portalControllerContext, PersonCreationForm form);


    /**
     * Remove input accents.
     * @param input input value
     * @return cleared input
     */
    String removeAccents(String input);


    /**
     * Proceed person registration.
     * @param portalControllerContext portal controller context
     */
    void proceedRegistration(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get terms of service URL.
     * @param portalControllerContext portal controller context
     * @return URL
     */
    String getTermsOfServiceUrl(PortalControllerContext portalControllerContext) throws PortletException;

}
