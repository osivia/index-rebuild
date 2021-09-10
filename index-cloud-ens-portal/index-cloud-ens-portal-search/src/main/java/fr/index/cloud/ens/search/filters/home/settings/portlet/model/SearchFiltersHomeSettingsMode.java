package fr.index.cloud.ens.search.filters.home.settings.portlet.model;

import org.apache.commons.lang.StringUtils;

/**
 * Search filters home settings modes enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum SearchFiltersHomeSettingsMode {

    /**
     * Form input mode.
     */
    FORM,
    /**
     * Filter selection mode.
     */
    FILTER;


    /**
     * Default mode.
     */
    public static final SearchFiltersHomeSettingsMode DEFAULT = FORM;


    /**
     * Identifier.
     */
    private final String id;
    /**
     * Internationalization key.
     */
    private final String key;


    /**
     * Constructor.
     */
    SearchFiltersHomeSettingsMode() {
        this.id = StringUtils.lowerCase(this.name());
        this.key = "SEARCH_FILTERS_HOME_SETTINGS_MODE_" + StringUtils.upperCase(this.name());
    }


    /**
     * Get mode from identifier.
     *
     * @param id identifier
     * @return mode
     */
    public static SearchFiltersHomeSettingsMode fromId(String id) {
        SearchFiltersHomeSettingsMode result = DEFAULT;
        for (SearchFiltersHomeSettingsMode value : SearchFiltersHomeSettingsMode.values()) {
            if (StringUtils.equals(id, value.id)) {
                result = value;
            }
        }
        return result;
    }


    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }
}
