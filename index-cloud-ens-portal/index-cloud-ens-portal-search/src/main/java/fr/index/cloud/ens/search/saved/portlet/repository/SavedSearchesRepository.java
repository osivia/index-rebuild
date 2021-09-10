package fr.index.cloud.ens.search.saved.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepository;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * Saved searches portlet repository interface.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepository
 */
public interface SavedSearchesRepository extends SearchCommonRepository {

    /**
     * Save user saved searches.
     *
     * @param portalControllerContext portal controller context
     * @param categoryId              saved searches category identifier
     * @param searchId                saved search identifier
     */
    void deleteSavedSearch(PortalControllerContext portalControllerContext, String categoryId, int searchId) throws PortletException;

}
