package fr.index.cloud.ens.search.filters.home.settings.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonService;
import fr.index.cloud.ens.search.filters.home.settings.portlet.model.SearchFiltersHomeSettingsForm;
import net.sf.json.JSONArray;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import java.io.IOException;

/**
 * Search filters home settings portlet service interface.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonService
 */
public interface SearchFiltersHomeSettingsService extends SearchCommonService {

    /**
     * Portlet instance.
     */
    String PORTLET_INSTANCE = "index-cloud-ens-search-filters-home-settings-instance";

    /**
     * Home settings user saved searches category identifier.
     */
    String HOME_SETTINGS_CATEGORY_ID = "home-settings";

    /**
     * User saved search identifier selector identifier.
     */
    String SAVED_SEARCH_ID_SELECTOR_ID = "savedSearchId";


    /**
     * Get form.
     *
     * @param portalControllerContext portal controller context
     * @return form
     */
    SearchFiltersHomeSettingsForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Save.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     */
    void save(PortalControllerContext portalControllerContext, SearchFiltersHomeSettingsForm form) throws PortletException, IOException;


    /**
     * Load user saved searches.
     *
     * @param portalControllerContext portal controller context
     * @return JSON array
     */
    JSONArray loadSavedSearches(PortalControllerContext portalControllerContext) throws PortletException;

}
