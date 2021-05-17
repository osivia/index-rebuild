package fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * File browser columns configuration form java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileBrowserColumnsConfigurationForm {

    /**
     * File browser identifier.
     */
    private String fileBrowserId;
    /**
     * Items.
     */
    private List<FileBrowserColumnsConfigurationItem> items;


    /**
     * Constructor.
     */
    public FileBrowserColumnsConfigurationForm() {
        super();
    }


    public String getFileBrowserId() {
        return fileBrowserId;
    }

    public void setFileBrowserId(String fileBrowserId) {
        this.fileBrowserId = fileBrowserId;
    }

    public List<FileBrowserColumnsConfigurationItem> getItems() {
        return items;
    }

    public void setItems(List<FileBrowserColumnsConfigurationItem> items) {
        this.items = items;
    }
}
