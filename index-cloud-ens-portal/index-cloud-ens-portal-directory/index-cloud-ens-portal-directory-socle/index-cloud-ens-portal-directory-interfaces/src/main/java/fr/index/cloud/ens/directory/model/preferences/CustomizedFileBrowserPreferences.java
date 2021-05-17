package fr.index.cloud.ens.directory.model.preferences;

import java.util.List;

/**
 * Customized file browser preferences interface.
 *
 * @author Cédric Krommenhoek
 */
public interface CustomizedFileBrowserPreferences {

    /**
     * Get file browser identifier.
     *
     * @return identifier
     */
    String getId();


    /**
     * Get file browser columns.
     *
     * @return columns
     */
    List<CustomizedFileBrowserColumn> getColumns();


    /**
     * Set file browser columns.
     *
     * @param columns columns
     */
    void setColumns(List<CustomizedFileBrowserColumn> columns);

}
