package fr.index.cloud.ens.dashboard;

import org.apache.commons.lang.StringUtils;

/**
 * Dashboard sort properties enumeration.
 */
public enum DashboardSort {

    /** Application. */
    APPLICATION;


    /** Identifier. */
    private final String id;


    /**
     * Constructor.
     */
    DashboardSort() {
        this.id = StringUtils.lowerCase(this.name());
    }


    /**
     * Get sort from identifier.
     *
     * @param id identifier
     * @return sort
     */
    public static DashboardSort fromId(String id) {
        DashboardSort result = APPLICATION;

        for (DashboardSort value : DashboardSort.values()) {
            if (StringUtils.equalsIgnoreCase(id, value.id)) {
                result = value;
                break;
            }
        }

        return result;
    }


    public String getId() {
        return id;
    }

}
