package fr.index.cloud.ens.search.filters.portlet.model;

import org.apache.commons.lang.StringUtils;

/**
 * Search filters size units enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum SearchFiltersSizeUnit {

    /**
     * Byte.
     */
    BYTE(0),
    /**
     * Kilobyte.
     */
    KILOBYTE(1),
    /**
     * Megabyte.
     */
    MEGABYTE(2),
    /**
     * Gigabyte.
     */
    GIGABYTE(3);


    /**
     * Default size unit.
     */
    public static final SearchFiltersSizeUnit DEFAULT = MEGABYTE;


    /**
     * Size unit internationalization key.
     */
    private final String key;
    /**
     * Size unit factor.
     */
    private final int factor;


    /**
     * Constructor.
     */
    SearchFiltersSizeUnit(int factor) {
        this.key = "SEARCH_FILTERS_SIZE_UNIT_" + StringUtils.upperCase(this.name());
        this.factor = factor;
    }


    public String getKey() {
        return key;
    }

    public int getFactor() {
        return factor;
    }
}
