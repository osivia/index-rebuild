package fr.index.cloud.ens.search.filters.portlet.model;

import org.apache.commons.lang.StringUtils;

/**
 * Search filters views enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum SearchFiltersView {

    /**
     * Default view.
     */
    DEFAULT,
    /**
     * Mutualized space view.
     */
    MUTUALIZED_SPACE,
    /**
     * Home settings view.
     */
    HOME_SETTINGS;


    /** Identifier. */
    private final String id;


    /**
     * Constructor.
     */
    SearchFiltersView() {
        this.id = StringUtils.lowerCase(StringUtils.replace(this.name(), "_", "-"));
    }


    public String getId() {
        return id;
    }
}
