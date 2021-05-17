package fr.index.cloud.ens.filebrowser.commons.portlet.service;

import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserColumn;
import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserPreferences;
import fr.index.cloud.ens.directory.model.preferences.CustomizedUserPreferences;
import fr.index.cloud.ens.directory.service.preferences.CustomizedUserPreferencesService;
import fr.index.cloud.ens.filebrowser.columns.configuration.portlet.service.FileBrowserColumnsConfigurationService;
import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserColumn;
import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserForm;
import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserItem;
import fr.index.cloud.ens.filebrowser.commons.portlet.model.CustomizedFileBrowserSortField;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.model.MutualizedFileBrowserSortEnum;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.service.MutualizedFileBrowserService;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserForm;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserSortEnum;
import fr.index.cloud.ens.filebrowser.portlet.service.CustomizedFileBrowserService;
import fr.toutatice.portail.cms.nuxeo.api.PageSelectors;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.services.workspace.filebrowser.portlet.repository.FileBrowserRepository;
import org.osivia.services.workspace.filebrowser.portlet.service.FileBrowserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.util.*;

/**
 * File browser portlet service implementation abstract super-class.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserServiceImpl
 * @see AbstractFileBrowserService
 */
public abstract class AbstractFileBrowserServiceImpl extends FileBrowserServiceImpl implements AbstractFileBrowserService {

    private static final String CMS_TASK_FILTER = "__CMS_TASK__";

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portlet repository.
     */
    @Autowired
    private FileBrowserRepository repository;

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
     * Constructor.
     */
    public AbstractFileBrowserServiceImpl() {
        super();
    }


    @Override
    public AbstractFileBrowserForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Selectors
        Map<String, List<String>> selectors = PageSelectors.decodeProperties(request.getParameter(SELECTORS_PARAMETER));

        // Form
        AbstractFileBrowserForm form = this.applicationContext.getBean(AbstractFileBrowserForm.class);

        if (!form.isInitialized()) {
            this.initializeForm(portalControllerContext, form);

            // User preferences
            CustomizedUserPreferences userPreferences;
            try {
                userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
            } catch (PortalException e) {
                throw new PortletException(e);
            }

            // Columns
            List<AbstractFileBrowserColumn> columns = this.getFileBrowserColumns(portalControllerContext, userPreferences);
            form.setColumns(columns);

            // Search filter title
            String searchFilterTitle;
            String searchFilterParameter = request.getParameter(SEARCH_FILTER_PARAMETER);
            if (StringUtils.isEmpty(searchFilterParameter)) {
                searchFilterTitle = null;
            } else if (searchFilterParameter.startsWith(CMS_TASK_FILTER)){
                String title = searchFilterParameter.substring(CMS_TASK_FILTER.length());
                searchFilterTitle = title;
            } else  {
                int id = NumberUtils.toInt(searchFilterParameter);
                Map<String, List<UserSavedSearch>> categorizedSavedSearches = userPreferences.getCategorizedSavedSearches();
                if (MapUtils.isEmpty(categorizedSavedSearches)) {
                    searchFilterTitle = null;
                } else {
                    Predicate predicate;
                    try {
                        predicate = EqualPredicate.getInstance(this.userPreferencesService.createUserSavedSearch(portalControllerContext, id));
                    } catch (PortalException e) {
                        throw new PortletException(e);
                    }

                    UserSavedSearch savedSearch = null;
                    Iterator<List<UserSavedSearch>> iterator = categorizedSavedSearches.values().iterator();
                    while ((savedSearch == null) && iterator.hasNext()) {
                        List<UserSavedSearch> savedSearches = iterator.next();
                        savedSearch = (UserSavedSearch) CollectionUtils.find(savedSearches, predicate);
                    }

                    if (savedSearch == null) {
                        searchFilterTitle = null;
                    } else {
                        searchFilterTitle = savedSearch.getDisplayName();
                    }
                }
            }
            form.setSearchFilterTitle(searchFilterTitle);

            // Search filters counter
            int counter = 0;
            if (MapUtils.isNotEmpty(selectors)) {
                for (String id : this.getSupportedSelectors()) {
                    if (CollectionUtils.isNotEmpty(selectors.get(id))) {
                        counter++;
                    }
                }
            }
            form.setSearchFiltersCounter(counter);

            // Current document title
            if ((form instanceof CustomizedFileBrowserForm) && StringUtils.isNotEmpty(form.getPath()) && StringUtils.isEmpty(searchFilterTitle) && !form.isListMode()) {
                CustomizedFileBrowserForm customizedForm = (CustomizedFileBrowserForm) form;

                // Current document context
                NuxeoDocumentContext documentContext = this.repository.getDocumentContext(portalControllerContext, form.getPath());

                String title = documentContext.getDocument().getTitle();
                customizedForm.setTitle(title);
            }

            // Initialized indicator
            form.setInitialized(true);
        }

        return form;
    }


    /**
     * Get file browser columns.
     *
     * @return columns
     */
    protected abstract List<AbstractFileBrowserColumn> getFileBrowserColumns(PortalControllerContext portalControllerContext, CustomizedUserPreferences userPreferences);


    /**
     * Get file browser supported selectors.
     *
     * @return selectors
     */
    protected abstract List<String> getSupportedSelectors();


    /**
     * Get file browser preferences.
     *
     * @param userPreferences user preferences
     * @return file browser preferences
     */
    protected CustomizedFileBrowserPreferences getFileBrowserPreferences(CustomizedUserPreferences userPreferences) {
        Map<String, CustomizedFileBrowserPreferences> preferences = userPreferences.getFileBrowserPreferences();
        if (preferences == null) {
            preferences = new HashMap<>(1);
            userPreferences.setFileBrowserPreferences(preferences);
        }

        String fileBrowserId = this.getFileBrowserId();

        CustomizedFileBrowserPreferences fileBrowserPreferences = preferences.get(fileBrowserId);
        if (fileBrowserPreferences == null) {
            fileBrowserPreferences = this.userPreferencesService.generateFileBrowserPreferences(fileBrowserId);
            preferences.put(fileBrowserId, fileBrowserPreferences);

            // Default configuration
            List<CustomizedFileBrowserSortField> defaultConfiguration;
            if (StringUtils.equals(CustomizedFileBrowserService.FILE_BROWSER_ID, fileBrowserId)) {
                defaultConfiguration = CustomizedFileBrowserSortEnum.DEFAULT_CONFIGURATION;
            } else if (StringUtils.equals(MutualizedFileBrowserService.FILE_BROWSER_ID, fileBrowserId)) {
                defaultConfiguration = MutualizedFileBrowserSortEnum.DEFAULT_CONFIGURATION;
            } else {
                defaultConfiguration = null;
            }

            // Default columns
            if (CollectionUtils.isNotEmpty(defaultConfiguration)) {
                List<CustomizedFileBrowserColumn> defaultColumns = new ArrayList<>(defaultConfiguration.size());
                for (int i = 0; i < defaultConfiguration.size(); i++) {
                    CustomizedFileBrowserSortField field = defaultConfiguration.get(i);

                    CustomizedFileBrowserColumn column = this.userPreferencesService.generateFileBrowserColumn(field.getId());
                    column.setOrder(i);
                    column.setVisible(true);

                    defaultColumns.add(column);
                }
                fileBrowserPreferences.setColumns(defaultColumns);

                userPreferences.setUpdated(true);
            }
        }

        return fileBrowserPreferences;
    }


    @Override
    protected AbstractFileBrowserItem createItem(PortalControllerContext portalControllerContext, Document nuxeoDocument) {
        AbstractFileBrowserItem item = (AbstractFileBrowserItem) super.createItem(portalControllerContext, nuxeoDocument);

        // Document DTO
        DocumentDTO documentDto = item.getDocument();

        // Document types
        List<String> documentTypes = this.getPropertyListValues(documentDto, "idxcl:documentTypes");
        item.setDocumentTypes(documentTypes);

        // Levels
        List<String> levels = this.getPropertyListValues(documentDto, "idxcl:levels");
        item.setLevels(levels);

        // Subjects
        List<String> subjects = this.getPropertyListValues(documentDto, "idxcl:subjects");
        item.setSubjects(subjects);

        // Format
        Object format = documentDto.getProperties().get("idxcl:formatText");
        item.setFormat((String) format);

        return item;
    }


    /**
     * Get property list values.
     *
     * @param documentDto document DTO
     * @param name        property name
     * @return values
     */
    private List<String> getPropertyListValues(DocumentDTO documentDto, String name) {
        List<String> values;

        // Property
        Object property = documentDto.getProperties().get(name);

        if (property instanceof List) {
            List<?> list = (List<?>) property;
            values = new ArrayList<>(list.size());

            for (Object object : list) {
                if (object instanceof String) {
                    String value = (String) object;
                    values.add(value);
                }
            }
        } else {
            values = null;
        }

        return values;
    }


    @Override
    public String getColumnsConfigurationUrl(PortalControllerContext portalControllerContext) throws PortletException {
        String instance = FileBrowserColumnsConfigurationService.PORTLET_INSTANCE;
        Map<String, String> properties = new HashMap<>();
        properties.put(FileBrowserColumnsConfigurationService.FILE_BROWSER_ID_WINDOW_PROPERTY, this.getFileBrowserId());

        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, instance, properties, PortalUrlType.MODAL);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    /**
     * Get file browser identifier.
     *
     * @return identifier
     */
    protected abstract String getFileBrowserId();


    @Override
    public void resetSearch(PortalControllerContext portalControllerContext, AbstractFileBrowserForm form) {
        // Action response
        ActionResponse response = (ActionResponse) portalControllerContext.getResponse();

        // Reset selectors
        response.setRenderParameter(SELECTORS_PARAMETER, StringUtils.EMPTY);
        response.removePublicRenderParameter(SEARCH_FILTER_PARAMETER);

        // Refresh other portlet model attributes
        PageProperties.getProperties().setRefreshingPage(true);
    }

}
