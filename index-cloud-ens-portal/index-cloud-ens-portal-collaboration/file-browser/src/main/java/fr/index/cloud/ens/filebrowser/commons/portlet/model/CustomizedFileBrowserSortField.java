package fr.index.cloud.ens.filebrowser.commons.portlet.model;

import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortField;

/**
 * Customized file browser sort field.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserSortField
 */
public interface CustomizedFileBrowserSortField extends FileBrowserSortField {

    /**
     * Check if field is configurable.
     *
     * @return true if field is configurable
     */
    boolean isConfigurable();

}
