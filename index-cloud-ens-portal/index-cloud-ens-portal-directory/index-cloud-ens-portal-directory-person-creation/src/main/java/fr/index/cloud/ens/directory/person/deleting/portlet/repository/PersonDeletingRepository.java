package fr.index.cloud.ens.directory.person.deleting.portlet.repository;

import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * Person deleting portlet repository interface.
 *
 * @author Cédric Krommenhoek
 */
public interface PersonDeletingRepository {

    /**
     * Delete user workspace.
     *
     * @param portalControllerContext portal controller context
     * @return true if user workspace has been deleted
     */
    boolean deleteUserWorkspace(PortalControllerContext portalControllerContext) throws PortletException;

}
