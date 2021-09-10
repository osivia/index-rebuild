package fr.index.cloud.ens.search.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepository;

import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * Search portlet repository interface.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepository
 */
public interface SearchRepository extends SearchCommonRepository {

    /**
     * Get navigation path.
     *
     * @param portalControllerContext portal controller context
     * @return path
     */
    String getNavigationPath(PortalControllerContext portalControllerContext) throws PortletException;

    /**
     * Gets the document.
     *
     * @param portalControllerContext the portal controller context
     * @return the document
     */

    public Document getDocument(PortalControllerContext portalControllerContext) ;
    
}
