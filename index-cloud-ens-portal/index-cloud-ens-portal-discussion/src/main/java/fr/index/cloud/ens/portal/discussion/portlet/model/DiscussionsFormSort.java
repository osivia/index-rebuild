package fr.index.cloud.ens.portal.discussion.portlet.model;

import org.apache.commons.lang.StringUtils;

public enum DiscussionsFormSort {

    /**
     * Document sort.
     */
    DOCUMENT("document"),
    /**
     * Date sort.
     */
    DATE("date"),
    /**
     * Location.
     */
    LOCATION("location");


    /**
     * Identifier.
     */
    private final String id;


    /**
     * Constructor.
     *
     * @param id identifier
     */
    DiscussionsFormSort(String id) {
        this.id = id;
    }


    /**
     * Get sort from identifier.
     *
     * @param id identifier
     * @return sort
     */
    public static DiscussionsFormSort fromId(String id) {
        DiscussionsFormSort result = DOCUMENT;

        for (DiscussionsFormSort value : DiscussionsFormSort.values()) {
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
