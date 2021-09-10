package fr.index.cloud.ens.search.filters.home.settings.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonServiceImpl;
import fr.index.cloud.ens.search.filters.home.settings.portlet.model.SearchFiltersHomeSettingsForm;
import fr.index.cloud.ens.search.filters.home.settings.portlet.model.SearchFiltersHomeSettingsMode;
import fr.toutatice.portail.cms.nuxeo.api.PageSelectors;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.util.*;

/**
 * Search filters home settings portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonServiceImpl
 * @see SearchFiltersHomeSettingsService
 */
@Service
public class SearchFiltersHomeSettingsServiceImpl extends SearchCommonServiceImpl implements SearchFiltersHomeSettingsService {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * User preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;


    /**
     * Constructor.
     */
    public SearchFiltersHomeSettingsServiceImpl() {
        super();
    }


    @Override
    public SearchFiltersHomeSettingsForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // Data
        Map<String, List<String>> data = this.getData(userPreferences);

        // Form
        SearchFiltersHomeSettingsForm form = this.applicationContext.getBean(SearchFiltersHomeSettingsForm.class);

        if (MapUtils.isEmpty(data)) {
            form.setMode(SearchFiltersHomeSettingsMode.DEFAULT);
        } else {
            // User saved search
            UserSavedSearch savedSearch;
            int savedSearchId = NumberUtils.toInt(this.getValue(data, SAVED_SEARCH_ID_SELECTOR_ID));
            if (savedSearchId > 0) {
                savedSearch = this.findSavedSearch(portalControllerContext, userPreferences, savedSearchId);
            } else {
                savedSearch = null;
            }

            if (savedSearch == null) {
                form.setMode(SearchFiltersHomeSettingsMode.FORM);
                form.setLevels(data.get(LEVELS_SELECTOR_ID));
                form.setSubjects(data.get(SUBJECTS_SELECTOR_ID));
            } else {
                form.setMode(SearchFiltersHomeSettingsMode.FILTER);
                form.setSavedSearch(savedSearch);
            }
        }

        return form;
    }


    /**
     * Get data.
     *
     * @param userPreferences         user preferences
     * @return data
     */
    private Map<String, List<String>> getData(UserPreferences userPreferences) {
        // Saved searches
        List<UserSavedSearch> savedSearches = userPreferences.getSavedSearches(HOME_SETTINGS_CATEGORY_ID);

        // Saved search
        UserSavedSearch savedSearch;
        if (CollectionUtils.isEmpty(savedSearches)) {
            savedSearch = null;
        } else {
            savedSearch = savedSearches.get(0);
        }

        // Saved search data
        Map<String, List<String>> data;
        if (savedSearch == null) {
            data = null;
        } else {
            data = PageSelectors.decodeProperties(savedSearch.getData());
        }

        return data;
    }


    /**
     * Get data value.
     *
     * @param data data
     * @param id   identifier
     * @return value
     */
    private String getValue(Map<String, List<String>> data, String id) {
        List<String> values;
        if (MapUtils.isEmpty(data)) {
            values = null;
        } else {
            values = data.get(id);
        }

        String value;
        if (CollectionUtils.isEmpty(values)) {
            value = null;
        } else {
            value = values.get(0);
        }

        return value;
    }


    @Override
    public void save(PortalControllerContext portalControllerContext, SearchFiltersHomeSettingsForm form) throws PortletException, IOException {
        // Action response
        ActionResponse response = (ActionResponse) portalControllerContext.getResponse();

        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // Saved searches
        List<UserSavedSearch> savedSearches = userPreferences.getSavedSearches(HOME_SETTINGS_CATEGORY_ID);
        if (savedSearches == null) {
            savedSearches = new ArrayList<>(1);
        }

        // Saved search
        UserSavedSearch savedSearch;
        if (CollectionUtils.isEmpty(savedSearches)) {
            try {
                int id = this.userPreferencesService.generateUserSavedSearchId(portalControllerContext, userPreferences);
                savedSearch = this.userPreferencesService.createUserSavedSearch(portalControllerContext, id);
                savedSearches.add(savedSearch);
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        } else {
            savedSearch = savedSearches.get(0);
        }

        // Data
        Map<String, List<String>> data = new HashMap<>();
        if (SearchFiltersHomeSettingsMode.FORM.equals(form.getMode())) {
            data.put(LEVELS_SELECTOR_ID, form.getLevels());
            data.put(SUBJECTS_SELECTOR_ID, form.getSubjects());
        } else if (SearchFiltersHomeSettingsMode.FILTER.equals(form.getMode()) && (form.getSavedSearch() != null)) {
            data.put(SAVED_SEARCH_ID_SELECTOR_ID, Collections.singletonList(String.valueOf(form.getSavedSearch().getId())));
        }
        savedSearch.setData(PageSelectors.encodeProperties(data));

        // Update user preferences
        userPreferences.setSavedSearches(HOME_SETTINGS_CATEGORY_ID, savedSearches);
        userPreferences.setUpdated(true);

        // Redirection
        String redirectionUrl = this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
        response.sendRedirect(redirectionUrl);
    }


    /**
     * Find user saved search.
     *
     * @param portalControllerContext portal controller context
     * @param userPreferences         user preferences
     * @param id                      user saved search identifier
     * @return user saved search
     */
    private UserSavedSearch findSavedSearch(PortalControllerContext portalControllerContext, UserPreferences userPreferences, int id) throws PortletException {
        // User categorized saved searches
        Map<String, List<UserSavedSearch>> categorizedSavedSearches = userPreferences.getCategorizedSavedSearches();

        UserSavedSearch result = null;
        if (MapUtils.isNotEmpty(categorizedSavedSearches)) {
            Predicate predicate;
            try {
                predicate = EqualPredicate.getInstance(this.userPreferencesService.createUserSavedSearch(portalControllerContext, id));
            } catch (PortalException e) {
                throw new PortletException(e);
            }

            Iterator<List<UserSavedSearch>> iterator = categorizedSavedSearches.values().iterator();
            while ((result == null) && iterator.hasNext()) {
                List<UserSavedSearch> savedSearches = iterator.next();
                result = (UserSavedSearch) CollectionUtils.find(savedSearches, predicate);
            }
        }

        return result;
    }


    @Override
    public JSONArray loadSavedSearches(PortalControllerContext portalControllerContext) throws PortletException {
        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // User saved searches
        List<UserSavedSearch> savedSearches = userPreferences.getSavedSearches(StringUtils.EMPTY);

        JSONArray array = new JSONArray();
        if (CollectionUtils.isNotEmpty(savedSearches)) {
            for (UserSavedSearch savedSearch : savedSearches) {
                JSONObject object = new JSONObject();
                object.put("id", savedSearch.getId());
                object.put("text", savedSearch.getDisplayName());
                array.add(object);
            }
        }

        return array;
    }

}
