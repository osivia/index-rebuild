package fr.index.cloud.ens.search.filters.portlet.model;

import org.apache.commons.lang.StringUtils;

/**
 * Search filters date ranges enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum SearchFiltersDateRange {

    /**
     * Unset.
     */
    UNSET,
    /**
     * Today.
     */
    TODAY(0),
    /**
     * Yesterday.
     */
    YESTERDAY(-1),
    /**
     * Week.
     */
    WEEK(-7),
    /**
     * Month.
     */
    MONTH(-30),
    /**
     * Trimester.
     */
    TRIMESTER(-90),
    /**
     * Customized.
     */
    CUSTOMIZED(true);


    /**
     * Default date range.
     */
    public static final SearchFiltersDateRange DEFAULT = UNSET;


    /**
     * Internationalization key.
     */
    private final String key;
    /**
     * Time offset.
     */
    private final Integer offset;
    /**
     * Customized date indicator.
     */
    private final boolean customized;


    /**
     * Constructor.
     */
    SearchFiltersDateRange() {
        this(null, false);
    }

    /**
     * Constructor.
     *
     * @param offset time offset
     */
    SearchFiltersDateRange(Integer offset) {
        this(offset, false);
    }

    /**
     * Constructor.
     *
     * @param customized customized date indicator
     */
    SearchFiltersDateRange(boolean customized) {
        this(null, customized);
    }

    /**
     * Constructor.
     *
     * @param offset     time offset
     * @param customized customized date indicator
     */
    SearchFiltersDateRange(Integer offset, boolean customized) {
        this.key = "SEARCH_FILTERS_DATE_RANGE_" + StringUtils.upperCase(this.name());
        this.offset = offset;
        this.customized = customized;
    }


    public String getKey() {
        return key;
    }

    public Integer getOffset() {
        return offset;
    }

    public boolean isCustomized() {
        return customized;
    }
}
