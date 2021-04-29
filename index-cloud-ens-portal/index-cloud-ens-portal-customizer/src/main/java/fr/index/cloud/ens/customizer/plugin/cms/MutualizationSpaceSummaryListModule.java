package fr.index.cloud.ens.customizer.plugin.cms;

import fr.toutatice.portail.cms.nuxeo.api.PageSelectors;
import fr.toutatice.portail.cms.nuxeo.api.portlet.PrivilegedPortletModule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.DirServiceFactory;

import javax.portlet.PortletContext;
import java.util.*;

/**
 * Mutualization space summary list module.
 *
 * @author Cédric Krommenhoek
 * @see PrivilegedPortletModule
 */
public class MutualizationSpaceSummaryListModule extends PrivilegedPortletModule {

    /**
     * Home settings user saved searches category identifier.
     */
    private static final String HOME_SETTINGS_CATEGORY_ID = "home-settings";

    /**
     * Keywords selector identifier.
     */
    private static final String KEYWORDS_SELECTOR_ID = "keywords";
    /**
     * Document types selector identifier.
     */
    private static final String DOCUMENT_TYPES_SELECTOR_ID = "documentTypes";
    /**
     * Levels selector identifier.
     */
    private static final String LEVELS_SELECTOR_ID = "levels";
    /**
     * Subjects selector identifier.
     */
    private static final String SUBJECTS_SELECTOR_ID = "subjects";
    
    /**
     * Subjects selector identifier.
     */
    private static final String FORMATS_SELECTOR_ID = "formats";
    
    /**
     * User saved search identifier selector identifier.
     */
    private static final String SAVED_SEARCH_ID_SELECTOR_ID = "savedSearchId";


    /**
     * Log.
     */
    private final Log log;

    /**
     * User preferences service.
     */
    private final UserPreferencesService userPreferencesService;


    /**
     * Constructor.
     *
     * @param portletContext portlet context
     */
    public MutualizationSpaceSummaryListModule(PortletContext portletContext) {
        super(portletContext);
        this.log = LogFactory.getLog(this.getClass());

        // User preferences service
        this.userPreferencesService = DirServiceFactory.getService(UserPreferencesService.class);
    }


    @Override
    public String getFilter(PortalControllerContext portalControllerContext) {
        String filter;

        try {
            // User preferences
            UserPreferences userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);

            // Data
            Map<String, List<String>> data = this.getData(portalControllerContext, userPreferences);
            // Selectors
            Map<String, List<String>> selectors;

            // User saved search identifier
            int savedSearchId = NumberUtils.toInt(this.getValue(data, SAVED_SEARCH_ID_SELECTOR_ID));

            if (savedSearchId > 0) {
                UserSavedSearch savedSearch = this.findSavedSearch(portalControllerContext, userPreferences, savedSearchId);
                if (savedSearch == null) {
                    selectors = null;
                } else {
                    selectors = PageSelectors.decodeProperties(savedSearch.getData());
                }
            } else {
                selectors = data;
            }

            filter = this.getFilter(portalControllerContext.getRequest().getRemoteUser(), selectors);
        } catch (PortalException e) {
            filter = null;
            this.log.error(e.getMessage());
        }

        return filter;
    }


    /**
     * Get data.
     *
     * @param portalControllerContext portal controller context
     * @param userPreferences         user preferences
     * @return data
     */
    private Map<String, List<String>> getData(PortalControllerContext portalControllerContext, UserPreferences userPreferences) {
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


    /**
     * Find user saved search.
     *
     * @param portalControllerContext portal controller context
     * @param userPreferences         user preferences
     * @param id                      user saved search identifier
     * @return user saved search
     */
    private UserSavedSearch findSavedSearch(PortalControllerContext portalControllerContext, UserPreferences userPreferences, int id) throws PortalException {
        // User categorized saved searches
        Map<String, List<UserSavedSearch>> categorizedSavedSearches = userPreferences.getCategorizedSavedSearches();

        UserSavedSearch result = null;
        if (MapUtils.isNotEmpty(categorizedSavedSearches)) {
            Predicate predicate = EqualPredicate.getInstance(this.userPreferencesService.createUserSavedSearch(portalControllerContext, id));

            Iterator<List<UserSavedSearch>> iterator = categorizedSavedSearches.values().iterator();
            while ((result == null) && iterator.hasNext()) {
                List<UserSavedSearch> savedSearches = iterator.next();
                result = (UserSavedSearch) CollectionUtils.find(savedSearches, predicate);
            }
        }

        return result;
    }



    /**
     * Gets the filter.
     *
     * @param userName the user name
     * @param selectors the selectors
     * @return the filter
     */
    private String getFilter(String userName, Map<String, List<String>> selectors) {
        List<String> filters = new ArrayList<>();
        
        if( userName != null)   {
            filters.add("dc:lastContributor <> '" + userName+"'");
         }

        if (MapUtils.isNotEmpty(selectors)) {
            // Keywords
            String keywords = StringUtils.join(selectors.get(KEYWORDS_SELECTOR_ID), " ");
            if (StringUtils.isNotBlank(keywords)) {
                filters.add("ecm:fulltext = '" + keywords + "' OR dc:title ILIKE '" + keywords + "%'");
            }

            // Document types
            List<String> documentTypes = selectors.get(DOCUMENT_TYPES_SELECTOR_ID);
            if (CollectionUtils.isNotEmpty(documentTypes)) {
                filters.add("idxcl:documentTypes IN ('" + StringUtils.join(documentTypes, "', '") + "')");
            }

            // Levels
            List<String> levels = selectors.get(LEVELS_SELECTOR_ID);
            if (CollectionUtils.isNotEmpty(levels)) {
                filters.add("idxcl:levelsTree STARTSWITH '" + StringUtils.join(levels, "' OR idxcl:levelsTree STARTSWITH '") + "'");
            }

            // Subjects
            List<String> subjects = selectors.get(SUBJECTS_SELECTOR_ID);
            if (CollectionUtils.isNotEmpty(subjects)) {
                filters.add("idxcl:subjectsTree STARTSWITH '" + StringUtils.join(subjects, "' OR idxcl:subjectsTree STARTSWITH '") + "'");
            }
            
            // Subjects
            List<String> formats = selectors.get(FORMATS_SELECTOR_ID);
            if (CollectionUtils.isNotEmpty(formats)) {
                filters.add("idxcl:format IN ('" + StringUtils.join(formats, "', '") + "')");
            }
        }

        // Filter
        String filter;
        if (CollectionUtils.isEmpty(filters)) {
            filter = null;
        } else {
            filter = "(" + StringUtils.join(filters, ") AND (") + ")";
        }

        return filter;
    }

}
