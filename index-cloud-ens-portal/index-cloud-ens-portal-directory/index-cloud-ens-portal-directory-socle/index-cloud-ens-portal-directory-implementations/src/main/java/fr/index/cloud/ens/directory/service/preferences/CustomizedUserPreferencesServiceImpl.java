package fr.index.cloud.ens.directory.service.preferences;

import fr.index.cloud.ens.directory.model.preferences.*;
import fr.index.cloud.ens.directory.repository.preferences.UpdateCustomizedUserPreferencesCommand;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.service.preferences.UserPreferencesServiceImpl;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User preferences service customized implementation.
 *
 * @author Cédric Krommenhoek
 * @see UserPreferencesServiceImpl
 * @see CustomizedUserPreferencesService
 */
@Service
@Primary
public class CustomizedUserPreferencesServiceImpl extends UserPreferencesServiceImpl implements CustomizedUserPreferencesService {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;


    /**
     * Constructor.
     */
    public CustomizedUserPreferencesServiceImpl() {
        super();
    }


    @Override
    public CustomizedUserPreferences getUserPreferences(PortalControllerContext portalControllerContext) throws PortalException {
        // User preferences
        UserPreferences userPreferences = super.getUserPreferences(portalControllerContext);
        // Customized user preferences
        CustomizedUserPreferences customizedUserPreferences;

        if (userPreferences instanceof CustomizedUserPreferences) {
            customizedUserPreferences = (CustomizedUserPreferences) userPreferences;
        } else {
            customizedUserPreferences = null;
        }

        return customizedUserPreferences;
    }


    @Override
    protected UserPreferences createUserPreferences(Document profile) {
        // User preferences
        UserPreferences userPreferences = super.createUserPreferences(profile);

        if (userPreferences instanceof CustomizedUserPreferences) {
            // Customized user preferences
            CustomizedUserPreferences customizedUserPreferences = (CustomizedUserPreferences) userPreferences;

            // File browser preferences
            Map<String, CustomizedFileBrowserPreferences> fileBrowserPreferences;
            PropertyList preferencesPropertyList = profile.getProperties().getList(UpdateCustomizedUserPreferencesCommand.CUSTOMIZED_FILE_BROWSER_PREFERENCES_XPATH);
            if ((preferencesPropertyList == null) || preferencesPropertyList.isEmpty()) {
                fileBrowserPreferences = new HashMap<>(0);
            } else {
                fileBrowserPreferences = new HashMap<>(preferencesPropertyList.size());
                for (int i = 0; i < preferencesPropertyList.size(); i++) {
                    PropertyMap preferencesPropertyMap = preferencesPropertyList.getMap(i);
                    String fileBrowserId = preferencesPropertyMap.getString("fileBrowserId");

                    if (StringUtils.isNotEmpty(fileBrowserId)) {
                        CustomizedFileBrowserPreferencesImpl preferences = this.applicationContext.getBean(CustomizedFileBrowserPreferencesImpl.class);
                        preferences.setId(fileBrowserId);

                        // Columns
                        PropertyList columnsPropertyList = preferencesPropertyMap.getList("fileBrowserColumns");
                        if ((columnsPropertyList != null) && !columnsPropertyList.isEmpty()) {
                            List<CustomizedFileBrowserColumn> columns = new ArrayList<>(columnsPropertyList.size());

                            for (int j = 0; j < columnsPropertyList.size(); j++) {
                                PropertyMap columnPropertyMap = columnsPropertyList.getMap(j);
                                String columnId = columnPropertyMap.getString("columnId");

                                if (StringUtils.isNotEmpty(columnId)) {
                                    CustomizedFileBrowserColumnImpl column = this.applicationContext.getBean(CustomizedFileBrowserColumnImpl.class);
                                    column.setId(columnId);

                                    int order = NumberUtils.toInt(columnPropertyMap.getString("order"));
                                    column.setOrder(order);

                                    boolean visible = BooleanUtils.isTrue(columnPropertyMap.getBoolean("visible"));
                                    column.setVisible(visible);

                                    columns.add(column);
                                }
                            }

                            preferences.setColumns(columns);
                        }

                        fileBrowserPreferences.put(fileBrowserId, preferences);
                    }
                }
            }
            customizedUserPreferences.setFileBrowserPreferences(fileBrowserPreferences);
        }

        return userPreferences;
    }


    @Override
    public CustomizedFileBrowserPreferences generateFileBrowserPreferences(String id) {
        CustomizedFileBrowserPreferencesImpl preferences = this.applicationContext.getBean(CustomizedFileBrowserPreferencesImpl.class);
        preferences.setId(id);

        return preferences;
    }


    @Override
    public CustomizedFileBrowserColumn generateFileBrowserColumn(String id) {
        CustomizedFileBrowserColumnImpl column = this.applicationContext.getBean(CustomizedFileBrowserColumnImpl.class);
        column.setId(id);

        return column;
    }

}
