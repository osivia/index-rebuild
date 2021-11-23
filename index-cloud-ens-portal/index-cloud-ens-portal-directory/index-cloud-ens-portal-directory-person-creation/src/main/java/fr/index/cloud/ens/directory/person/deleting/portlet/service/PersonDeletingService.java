package fr.index.cloud.ens.directory.person.deleting.portlet.service;

import fr.index.cloud.ens.directory.person.deleting.portlet.model.PersonDeletingForm;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * Person deleting portlet service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface PersonDeletingService {

    /**
     * Portlet instance.
     */
    String PORTLET_INSTANCE = "cloudens-person-deleting-portlet-instance";

    /**
     * View form indicator window property.
     */
    String VIEW_FORM_WINDOW_PROPERTY = "osivia.person.deleting.form";


    /**
     * Get view path.
     *
     * @param portalControllerContext portal controller context
     * @return view path
     */
    String getViewPath(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get person deleting form.
     *
     * @param portalControllerContext portal controller context
     * @return form
     */
    PersonDeletingForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get view form URL.
     *
     * @param portalControllerContext portal controller context
     * @return URL
     */
    String getViewFormUrl(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get cancel URL.
     *
     * @param portalControllerContext portal controller context
     * @return URL
     */
    String getCancelUrl(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Validate person deletion.
     *
     * @param portalControllerContext portal controller context
     * @param form                    person deleting form
     */
    void validate(PortalControllerContext portalControllerContext, PersonDeletingForm form) throws PortletException;


    /**
     * Delete person.
     *
     * @param portalControllerContext portal controller context
     * @param form                    person deleting form
     */
    void delete(PortalControllerContext portalControllerContext, PersonDeletingForm form) throws PortletException;

}
