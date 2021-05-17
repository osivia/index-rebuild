package fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * File browser columns configuration item java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileBrowserColumnsConfigurationItem {

    /** Identifier. */
    private String id;
    /** Title. */
    private String title;
    /** Order. */
    private int order;
    /** Visible. */
    private boolean visible;
    /** List mode indicator. */
    private boolean listMode;


    /**
     * Constructor.
     */
    public FileBrowserColumnsConfigurationItem() {
        super();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isListMode() {
        return listMode;
    }

    public void setListMode(boolean listMode) {
        this.listMode = listMode;
    }
}
