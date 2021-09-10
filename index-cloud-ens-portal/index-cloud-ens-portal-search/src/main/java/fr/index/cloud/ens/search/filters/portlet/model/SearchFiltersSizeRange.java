package fr.index.cloud.ens.search.filters.portlet.model;

import org.apache.commons.lang.StringUtils;

/**
 * Search filters size ranges enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum SearchFiltersSizeRange {

    /**
     * Less.
     */
    LESS,
    /**
     * More.
     */
    MORE;


    /** Default size range. */
    public static final SearchFiltersSizeRange DEFAULT = MORE;


    /** Size range internationalization key. */
    private final String key;


    /**
     * Constructor.
     */
    SearchFiltersSizeRange() {
        this.key = "SEARCH_FILTERS_SIZE_RANGE_" + StringUtils.upperCase(this.name());
    }


    public String getKey() {
        return key;
    }
}
