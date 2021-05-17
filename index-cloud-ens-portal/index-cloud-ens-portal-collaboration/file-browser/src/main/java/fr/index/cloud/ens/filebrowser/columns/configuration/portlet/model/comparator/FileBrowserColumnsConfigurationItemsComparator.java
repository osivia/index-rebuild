package fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.comparator;

import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.FileBrowserColumnsConfigurationItem;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * File browser columns configuration items comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see FileBrowserColumnsConfigurationItem
 */
@Component
public class FileBrowserColumnsConfigurationItemsComparator implements Comparator<FileBrowserColumnsConfigurationItem> {

    /**
     * Constructor.
     */
    public FileBrowserColumnsConfigurationItemsComparator() {
        super();
    }


    @Override
    public int compare(FileBrowserColumnsConfigurationItem item1, FileBrowserColumnsConfigurationItem item2) {
        return Integer.compare(item1.getOrder(), item2.getOrder());
    }

}
