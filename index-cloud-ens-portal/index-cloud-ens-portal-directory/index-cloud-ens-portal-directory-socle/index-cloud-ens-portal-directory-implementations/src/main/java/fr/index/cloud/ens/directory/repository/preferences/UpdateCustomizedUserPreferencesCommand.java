package fr.index.cloud.ens.directory.repository.preferences;

import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserColumn;
import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserPreferences;
import fr.index.cloud.ens.directory.model.preferences.CustomizedUserPreferences;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.directory.v2.repository.preferences.UpdateUserPreferencesCommand;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Update customized user preferences Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see UpdateUserPreferencesCommand
 */
@Component
@Primary
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UpdateCustomizedUserPreferencesCommand extends UpdateUserPreferencesCommand {

    /**
     * Customized file browser preferences.
     */
    public static final String CUSTOMIZED_FILE_BROWSER_PREFERENCES_XPATH = "idxup:fileBrowserPreferences";


    // User preferences
    private final CustomizedUserPreferences preferences;


    /**
     * Constructor.
     *
     * @param preferences user preferences
     */
    public UpdateCustomizedUserPreferencesCommand(CustomizedUserPreferences preferences) {
        super(preferences);
        this.preferences = preferences;
    }


    @Override
    protected PropertyMap getProperties() {
        PropertyMap properties = super.getProperties();

        // Customized file browser preferences
        String property;
        if (MapUtils.isEmpty(this.preferences.getFileBrowserPreferences())) {
            property = null;
        } else {
            JSONArray fileBrowsers = new JSONArray();

            for (CustomizedFileBrowserPreferences fileBrowserPreferences : this.preferences.getFileBrowserPreferences().values()) {
                JSONObject fileBrowser = new JSONObject();
                fileBrowser.put("fileBrowserId", fileBrowserPreferences.getId());

                JSONArray columns = new JSONArray();
                if (CollectionUtils.isNotEmpty(fileBrowserPreferences.getColumns())) {
                    for (CustomizedFileBrowserColumn fileBrowserColumn : fileBrowserPreferences.getColumns()) {
                        JSONObject column = new JSONObject();
                        column.put("columnId", fileBrowserColumn.getId());
                        column.put("order", fileBrowserColumn.getOrder());
                        column.put("visible", fileBrowserColumn.isVisible());

                        columns.add(column);
                    }
                }
                fileBrowser.put("fileBrowserColumns", columns);

                fileBrowsers.add(fileBrowser);
            }

            property = fileBrowsers.toString();
        }
        properties.set(CUSTOMIZED_FILE_BROWSER_PREFERENCES_XPATH, property);

        return properties;
    }

}
