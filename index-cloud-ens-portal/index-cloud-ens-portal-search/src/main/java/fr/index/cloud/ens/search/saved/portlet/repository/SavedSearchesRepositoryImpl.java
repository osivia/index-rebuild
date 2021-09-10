package fr.index.cloud.ens.search.saved.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepositoryImpl;
import org.apache.commons.collections.CollectionUtils;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.portlet.PortletException;
import java.util.List;

/**
 * Saved searches portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepositoryImpl
 * @see SavedSearchesRepository
 */
@Repository
public class SavedSearchesRepositoryImpl extends SearchCommonRepositoryImpl implements SavedSearchesRepository {

    /**
     * User preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;


    /**
     * Constructor.
     */
    public SavedSearchesRepositoryImpl() {
        super();
    }


    @Override
    public void deleteSavedSearch(PortalControllerContext portalControllerContext, String categoryId, int searchId) throws PortletException {
        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // User saved searches
        List<UserSavedSearch> savedSearches = userPreferences.getSavedSearches(categoryId);

        if (CollectionUtils.isNotEmpty(savedSearches)) {
            try {
                savedSearches.remove(this.userPreferencesService.createUserSavedSearch(portalControllerContext, searchId));
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        }

        userPreferences.setSavedSearches(categoryId, savedSearches);
        userPreferences.setUpdated(true);
    }

}
