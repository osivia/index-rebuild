package fr.index.cloud.ens.search.common.portlet.repository;

import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import net.sf.json.JSONArray;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import java.io.IOException;
import java.util.List;

/**
 * Search common repository interface.
 *
 * @author Cédric Krommenhoek
 */
public interface SearchCommonRepository {

    /**
     * Search filters task identifier.
     */
    String SEARCH_FILTERS_TASK_ID = "SEARCH_FILTERS";


    /**
     * Get navigation path.
     *
     * @param portalControllerContext portal controller context
     * @return navigation path
     */
    String getNavigationPath(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get document context.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return document context
     */
    NuxeoDocumentContext getDocumentContext(PortalControllerContext portalControllerContext, String path) throws PortletException;


    /**
     * Get user workspace path.
     *
     * @param portalControllerContext portal controller context
     * @return path
     */
    String getUserWorkspacePath(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get search path.
     *
     * @param portalControllerContext portal controller context
     * @return path
     */
    String getSearchPath(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get saved searches.
     *
     * @param portalControllerContext portal controller context
     * @param categoryId              saved searches category identifier
     * @return saved searches
     */
    List<UserSavedSearch> getSavedSearches(PortalControllerContext portalControllerContext, String categoryId) throws PortletException;


    /**
     * Load vocabulary.
     *
     * @param portalControllerContext portal controller context
     * @param vocabulary              vocabulary
     * @return vocabulary JSON array
     */
    JSONArray loadVocabulary(PortalControllerContext portalControllerContext, String vocabulary) throws PortletException, IOException;

}
