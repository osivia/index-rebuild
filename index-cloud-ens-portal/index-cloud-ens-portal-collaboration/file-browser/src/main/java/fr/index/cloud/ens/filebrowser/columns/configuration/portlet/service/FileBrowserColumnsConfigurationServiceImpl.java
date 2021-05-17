package fr.index.cloud.ens.filebrowser.columns.configuration.portlet.service;

import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserColumn;
import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserPreferences;
import fr.index.cloud.ens.directory.model.preferences.CustomizedUserPreferences;
import fr.index.cloud.ens.directory.service.preferences.CustomizedUserPreferencesService;
import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.FileBrowserColumnsConfigurationForm;
import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.FileBrowserColumnsConfigurationItem;
import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.model.comparator.FileBrowserColumnsConfigurationItemsComparator;
import fr.index.cloud.ens.filebrowser.commons.portlet.model.CustomizedFileBrowserSortField;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.model.MutualizedFileBrowserSortEnum;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.service.MutualizedFileBrowserService;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserSortEnum;
import fr.index.cloud.ens.filebrowser.portlet.service.CustomizedFileBrowserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.util.*;

/**
 * File browser columns configuration portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserColumnsConfigurationService
 */
@Service
public class FileBrowserColumnsConfigurationServiceImpl implements FileBrowserColumnsConfigurationService {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Items comparator.
     */
    @Autowired
    private FileBrowserColumnsConfigurationItemsComparator itemsComparator;


    /**
     * User preferences service.
     */
    @Autowired
    private CustomizedUserPreferencesService userPreferencesService;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public FileBrowserColumnsConfigurationServiceImpl() {
        super();
    }


    @Override
    public FileBrowserColumnsConfigurationForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        // File browser identifier
        String fileBrowserId = window.getProperty(FILE_BROWSER_ID_WINDOW_PROPERTY);

        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());


        // Saved columns
        Map<String, CustomizedFileBrowserColumn> savedColumns = this.getSavedColumns(portalControllerContext, fileBrowserId);

        // Configurable fields
        List<CustomizedFileBrowserSortField> configurableFields = this.getConfigurableFields(fileBrowserId);


        // Items
        List<FileBrowserColumnsConfigurationItem> items;
        if (CollectionUtils.isEmpty(configurableFields)) {
            items = null;
        } else {
            items = new ArrayList<>(configurableFields.size());

            for (int i = 0; i < configurableFields.size(); i++) {
                CustomizedFileBrowserSortField field = configurableFields.get(i);

                // Saved column
                CustomizedFileBrowserColumn savedColumn = savedColumns.get(field.getId());
                int order;
                boolean visible;
                if (savedColumn == null) {
                    order = configurableFields.size() + i;
                    visible = false;
                } else {
                    order = savedColumn.getOrder();
                    visible = savedColumn.isVisible();
                }

                // Item
                FileBrowserColumnsConfigurationItem item = this.applicationContext.getBean(FileBrowserColumnsConfigurationItem.class);
                item.setId(field.getId());
                item.setTitle(bundle.getString(field.getKey()));
                item.setOrder(order);
                item.setVisible(visible);
                item.setListMode(field.isListMode());

                items.add(item);
            }

            items.sort(this.itemsComparator);
        }


        // Form
        FileBrowserColumnsConfigurationForm form = this.applicationContext.getBean(FileBrowserColumnsConfigurationForm.class);
        form.setFileBrowserId(fileBrowserId);
        form.setItems(items);

        return form;
    }


    /**
     * Get saved columns.
     *
     * @param portalControllerContext portal controller context
     * @param fileBrowserId           file browser identifier
     * @return saved columns
     */
    private Map<String, CustomizedFileBrowserColumn> getSavedColumns(PortalControllerContext portalControllerContext, String fileBrowserId) throws PortletException {
        // File browser preferences
        CustomizedFileBrowserPreferences fileBrowserPreferences = this.getFileBrowserPreferences(portalControllerContext, fileBrowserId);

        // Saved columns
        Map<String, CustomizedFileBrowserColumn> savedColumns;
        if (fileBrowserPreferences == null) {
            savedColumns = new HashMap<>(0);
        } else {
            savedColumns = new HashMap<>(fileBrowserPreferences.getColumns().size());
            for (CustomizedFileBrowserColumn savedColumn : fileBrowserPreferences.getColumns()) {
                savedColumns.put(savedColumn.getId(), savedColumn);
            }
        }
        return savedColumns;
    }


    /**
     * Get file browser preferences.
     *
     * @param portalControllerContext portal controller context
     * @param fileBrowserId           file browser identifier
     * @return preferences
     */
    private CustomizedFileBrowserPreferences getFileBrowserPreferences(PortalControllerContext portalControllerContext, String fileBrowserId) throws PortletException {
        // User preferences
        CustomizedUserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // File browser preferences
        CustomizedFileBrowserPreferences fileBrowserPreferences;
        if (StringUtils.isEmpty(fileBrowserId) || MapUtils.isEmpty(userPreferences.getFileBrowserPreferences())) {
            fileBrowserPreferences = null;
        } else {
            fileBrowserPreferences = userPreferences.getFileBrowserPreferences().get(fileBrowserId);
        }
        return fileBrowserPreferences;
    }


    /**
     * Get configurable fields.
     *
     * @param fileBrowserId file browser identifier
     * @return fields
     */
    private List<CustomizedFileBrowserSortField> getConfigurableFields(String fileBrowserId) {
        // Fields
        List<CustomizedFileBrowserSortField> fields;
        if (StringUtils.equals(CustomizedFileBrowserService.FILE_BROWSER_ID, fileBrowserId)) {
            fields = Arrays.asList(CustomizedFileBrowserSortEnum.values());
        } else if (StringUtils.equals(MutualizedFileBrowserService.FILE_BROWSER_ID, fileBrowserId)) {
            fields = Arrays.asList(MutualizedFileBrowserSortEnum.values());
        } else {
            fields = null;
        }

        // Configurable fields
        List<CustomizedFileBrowserSortField> configurableFields;
        if (CollectionUtils.isEmpty(fields)) {
            configurableFields = null;
        } else {
            configurableFields = new ArrayList<>(fields.size());
            for (CustomizedFileBrowserSortField field : fields) {
                if (field.isConfigurable()) {
                    configurableFields.add(field);
                }
            }
        }
        return configurableFields;
    }


    @Override
    public void save(PortalControllerContext portalControllerContext, FileBrowserColumnsConfigurationForm form) throws PortletException {
        if (StringUtils.isNotEmpty(form.getFileBrowserId())) {
            // User preferences
            CustomizedUserPreferences userPreferences;
            try {
                userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
            } catch (PortalException e) {
                throw new PortletException(e);
            }

            // File browser preferences
            Map<String, CustomizedFileBrowserPreferences> fileBrowserPreferences = userPreferences.getFileBrowserPreferences();
            if (fileBrowserPreferences == null) {
                fileBrowserPreferences = new HashMap<>(1);
                userPreferences.setFileBrowserPreferences(fileBrowserPreferences);
            }

            CustomizedFileBrowserPreferences preferences = fileBrowserPreferences.get(form.getFileBrowserId());
            if (preferences == null) {
                preferences = this.userPreferencesService.generateFileBrowserPreferences(form.getFileBrowserId());
                fileBrowserPreferences.put(form.getFileBrowserId(), preferences);
            }

            // Overwrite columns
            List<CustomizedFileBrowserColumn> columns;
            if (CollectionUtils.isEmpty(form.getItems())) {
                columns = null;
            } else {
                columns = new ArrayList<>(form.getItems().size());
                for (FileBrowserColumnsConfigurationItem item : form.getItems()) {
                    CustomizedFileBrowserColumn column = this.userPreferencesService.generateFileBrowserColumn(item.getId());
                    column.setOrder(item.getOrder());
                    column.setVisible(item.isVisible());

                    columns.add(column);
                }
            }

            preferences.setColumns(columns);
            userPreferences.setUpdated(true);
        }
    }


    @Override
    public String getRedirectionUrl(PortalControllerContext portalControllerContext) {
        return this.portalUrlFactory.getBackURL(portalControllerContext, false, true);
    }

}
