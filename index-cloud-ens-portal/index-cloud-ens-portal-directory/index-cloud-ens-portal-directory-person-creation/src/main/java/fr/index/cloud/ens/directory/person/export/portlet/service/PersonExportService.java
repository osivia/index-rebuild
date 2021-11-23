/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.service;

import java.text.ParseException;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

import fr.index.cloud.ens.directory.person.export.portlet.controller.Export;
import fr.index.cloud.ens.directory.person.export.portlet.controller.PersonExportForm;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;

/**
 * @author Loïc Billon
 *
 */
public interface PersonExportService {

    /** model identifier. */
    public static final String MODEL_ID = IFormsService.FORMS_WEB_ID_PREFIX + "export_workspace";

	/**
	 * Launch an export
	 * 
	 * @param portalControllerContext
	 * @param uid 
	 * @param userWorkspacePath
	 */
	void prepareBatch(PortalControllerContext portalControllerContext, String uid, String userWorkspacePath, PersonExportForm form) throws ParseException, PortalException;

	/**
	 * Get all exports procedures
	 * 
	 * @param portalControllerContext
	 * @return
	 */
	PersonExportForm getForm(PortalControllerContext portalControllerContext);

	/**
	 * Remove a procedure
	 * 
	 * @param portalControllerContext
	 * @param export
	 */
	void remove(PortalControllerContext portalControllerContext, Export export, PersonExportForm form);

}