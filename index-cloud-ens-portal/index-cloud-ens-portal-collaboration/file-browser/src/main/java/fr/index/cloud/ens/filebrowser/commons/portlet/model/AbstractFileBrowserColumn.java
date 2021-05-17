package fr.index.cloud.ens.filebrowser.commons.portlet.model;

import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortField;

/**
 * File browser column abstract super-class.
 *
 * @author Cédric Krommenhoek
 */
public abstract class AbstractFileBrowserColumn implements Comparable<AbstractFileBrowserColumn> {

    /**
     * Column identifier.
     */
    private String id;
    /**
     * Column order.
     */
    private int order;


    /**
     * Constructor.
     */
    public AbstractFileBrowserColumn() {
        super();
    }


    @Override
    public int compareTo(AbstractFileBrowserColumn other) {
        return Integer.compare(this.order, other.order);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
