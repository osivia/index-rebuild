package fr.index.cloud.ens.filebrowser.mutualized.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.CustomizedFileBrowserSortField;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortField;

/**
 * Mutualized file browser sort field interface.
 *
 * @author Cédric Krommenhoek
 * @see CustomizedFileBrowserSortField
 */
public interface MutualizedFileBrowserSortField extends CustomizedFileBrowserSortField {

    /**
     * Get NXQL field.
     *
     * @return field
     */
    String getField();

}
