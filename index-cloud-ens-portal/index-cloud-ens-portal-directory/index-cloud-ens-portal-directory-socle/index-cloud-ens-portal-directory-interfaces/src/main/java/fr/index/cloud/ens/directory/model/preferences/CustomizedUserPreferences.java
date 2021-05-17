package fr.index.cloud.ens.directory.model.preferences;

import org.osivia.directory.v2.model.preferences.UserPreferences;

import java.util.List;
import java.util.Map;

/**
 * User preferences customized interface.
 *
 * @author Cédric Krommenhoek
 * @see UserPreferences
 */
public interface CustomizedUserPreferences extends UserPreferences {

    /**
     * Get file browser preferences.
     *
     * @return preferences
     */
    Map<String, CustomizedFileBrowserPreferences> getFileBrowserPreferences();


    /**
     * Set file browser preferences.
     *
     * @param preferences file browser preferences
     */
    void setFileBrowserPreferences(Map<String, CustomizedFileBrowserPreferences> preferences);

}
