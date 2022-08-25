package fr.index.cloud.ens.taskbar.portlet.repository;

import fr.index.cloud.ens.taskbar.portlet.model.*;
import fr.index.cloud.ens.taskbar.portlet.model.comparator.FolderTaskComparator;
import fr.index.cloud.ens.taskbar.portlet.model.comparator.SavedSearchComparator;
import fr.index.cloud.ens.taskbar.portlet.repository.command.LoadVocabularyCommand;
import fr.index.cloud.ens.taskbar.portlet.repository.command.MoveDocumentsCommand;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoPermissions;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoCustomizer;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.Link;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.cms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import javax.portlet.PortletException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Taskbar portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see TaskbarRepository
 */
@Repository
public class TaskbarRepositoryImpl implements TaskbarRepository {

    /**
     * Document edition portlet instance.
     */
    private static final String DOCUMENT_EDITION_PORTLET_INSTANCE = "osivia-services-document-edition-instance";


    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /**
     * Taskbar service.
     */
    @Autowired
    private ITaskbarService taskbarService;

    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;

    /**
     * CMS service locator.
     */
    @Autowired
    private ICMSServiceLocator cmsServiceLocator;

    /**
     * Nuxeo service.
     */
    @Autowired
    private INuxeoService nuxeoService;

    /**
     * Folder task comparator.
     */
    @Autowired
    private FolderTaskComparator folderTaskComparator;

    /**
     * Saved search comparator.
     */
    @Autowired
    private SavedSearchComparator savedSearchComparator;

    /**
     * User preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;


    /**
     * Constructor.
     */
    public TaskbarRepositoryImpl() {
        super();
    }


    @Override
    public String getBasePath(PortalControllerContext portalControllerContext, TaskbarWindowProperties windowProperties) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Path window property, may be null
        String path = windowProperties.getPath();

        // Base path
        String basePath;
        if (StringUtils.isEmpty(path)) {
            basePath = nuxeoController.getBasePath();
        } else {
            basePath = nuxeoController.getComputedPath(path);
        }

        return basePath;
    }


    @Override
    public List<TaskbarTask> getNavigationTasks(PortalControllerContext portalControllerContext, String basePath) throws PortletException {
        // Navigation tasks
        List<TaskbarTask> navigationTasks;
        if (StringUtils.isEmpty(basePath)) {
            navigationTasks = new ArrayList<>(0);
        } else {
            try {
                navigationTasks = this.taskbarService.getTasks(portalControllerContext, basePath);
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        }

        return navigationTasks;
    }


    @Override
    public List<AddDropdownItem> generateAddDropdownItems(PortalControllerContext portalControllerContext) throws PortletException {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Base path
        String basePath = nuxeoController.getBasePath();

        // Dropdown items
        List<AddDropdownItem> dropdownItems;

        if (StringUtils.isEmpty(basePath)) {
            dropdownItems = null;
        } else {
            // Documents path
            String documentsPath = basePath + "/documents";
            // Navigation path
            String navigationPath = nuxeoController.getNavigationPath();
            // Parent document path
            String parentPath;
            if (StringUtils.startsWith(navigationPath, documentsPath)) {
                parentPath = navigationPath;
            } else {
                parentPath = documentsPath;
            }

            // Document context
            NuxeoDocumentContext documentContext = nuxeoController.getDocumentContext(parentPath);
            // Document permissions
            NuxeoPermissions permissions;
            if (documentContext == null) {
                permissions = null;
            } else {
                permissions = documentContext.getPermissions();
            }

            if ((permissions != null) && permissions.isEditable()) {
                dropdownItems = new ArrayList<>();

                // Internationalization bundle
                Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
                // Nuxeo customizer
                INuxeoCustomizer nuxeoCustomizer = this.nuxeoService.getCMSCustomizer();
                // Document types
                Map<String, DocumentType> documentTypes = nuxeoCustomizer.getDocumentTypes();

                for (String type : Arrays.asList("Folder", "File")) {
                    AddDropdownItem dropdownItem = this.generateDocumentTypeDropdownItem(nuxeoController, bundle, documentTypes, parentPath, type);

                    if (dropdownItem != null) {
                        dropdownItems.add(dropdownItem);
                    }
                }
            } else {
                dropdownItems = null;
            }
        }

        return dropdownItems;
    }


    /**
     * Generate document type dropdown item.
     *
     * @param nuxeoController Nuxeo controller
     * @param bundle          internationalization bundle
     * @param documentTypes   document types
     * @param parentPath      parent document path
     * @param type            added document type
     * @return dropdown item
     */
    private AddDropdownItem generateDocumentTypeDropdownItem(NuxeoController nuxeoController, Bundle bundle, Map<String, DocumentType> documentTypes, String parentPath, String type) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = nuxeoController.getPortalCtx();

        // Dropdown item
        AddDropdownItem dropdownItem;

        // Document type
        DocumentType documentType = documentTypes.get(type);

        if (documentType == null) {
            dropdownItem = null;
        } else {
            // Display name
            String displayName;
            if (documentType.isFile()) {
                displayName = bundle.getString("TASKBAR_ADD_FILES");
            } else {
                displayName = bundle.getString(StringUtils.upperCase(type), documentType.getCustomizedClassLoader());
            }

            // Window properties
            Map<String, String> properties = new HashMap<>();
            properties.put("osivia.document.edition.base-path", nuxeoController.getBasePath());
            properties.put("osivia.document.edition.parent-path", parentPath);
            properties.put("osivia.document.edition.document-type", type);
            if (documentType.isFile()) {
                properties.put("osivia.document.edition.multiple-files", String.valueOf(true));
            }
            properties.put("osivia.document.edition.modal", String.valueOf(true));


            // URL
            String url;
            try {
                url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, DOCUMENT_EDITION_PORTLET_INSTANCE, properties, PortalUrlType.MODAL);
            } catch (PortalException e) {
                throw new PortletException(e);
            }

            // Modal title
            String modalTitle;
            if (documentType.isFile()) {
                modalTitle = bundle.getString("TASKBAR_DOCUMENT_CREATION_FILES");
            } else {
                modalTitle = bundle.getString("DOCUMENT_CREATION_" + StringUtils.upperCase(type));
            }

            // Task
            dropdownItem = this.applicationContext.getBean(AddDropdownItem.class);
            dropdownItem.setIcon(documentType.getIcon());
            dropdownItem.setDisplayName(displayName);
            dropdownItem.setUrl(url);
            dropdownItem.setModalTitle(modalTitle);
        }

        return dropdownItem;
    }


    @Override
    public Task generateFolderTask(PortalControllerContext portalControllerContext, String basePath, String path) throws PortletException {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Current navigation path
        String currentNavigationPath = nuxeoController.getNavigationPath();


        // Navigation item
        CMSItem navigationItem;
        try {
            navigationItem = cmsService.getPortalNavigationItem(cmsContext, basePath, path);
        } catch (CMSException e) {
            throw new PortletException(e);
        }

        // Folder task
        FolderTask task = this.getFolderTask(portalControllerContext, nuxeoController, currentNavigationPath, navigationItem);

        // Children
        this.generateFolderChildren(nuxeoController, basePath, task);

        return task;
    }


    /**
     * Get folder task.
     *
     * @param portalControllerContext portal controller context
     * @param currentNavigationPath   current navigation path
     * @param navigationItem          navigation item
     * @return task
     */
    private FolderTask getFolderTask(PortalControllerContext portalControllerContext, NuxeoController nuxeoController, String currentNavigationPath, CMSItem navigationItem) {
        FolderTask task = this.applicationContext.getBean(FolderTask.class);

        // Document type
        DocumentType documentType = navigationItem.getType();
        // Fetched children indicator
        Boolean unfetchedChildren = BooleanUtils.toBooleanObject(navigationItem.getProperties().get("unfetchedChildren"));
        // Nuxeo document
        Document document = (Document) navigationItem.getNativeItem();

        // Identifier
        String id = document.getId();
        task.setId(id);

        // Path
        String path = navigationItem.getCmsPath();
        task.setPath(path);

        // Display name
        String displayName = document.getTitle();
        task.setDisplayName(displayName);

        // URL
        Link link = nuxeoController.getLink(document, "menu");
        task.setUrl(link.getUrl());

        // Active indicator
        boolean active = StringUtils.equals(currentNavigationPath, path);
        task.setActive(active);

        // Selected indicator
        boolean selected = this.isSelected(path, currentNavigationPath);
        task.setSelected(selected);

        // Folder indicator
        boolean folder = (documentType != null) && documentType.isFolderish();
        task.setFolder(folder);

        // Lazy indicator
        boolean lazy = (documentType != null) && documentType.isBrowsable() && !selected && BooleanUtils.isNotFalse(unfetchedChildren);
        task.setLazy(lazy);

        // Accepted types
        String[] acceptedTypes = this.getAcceptedTypes(navigationItem);
        task.setAcceptedTypes(acceptedTypes);

        return task;
    }


    /**
     * Generate folder children.
     *
     * @param nuxeoController Nuxeo controller
     * @param basePath        base path
     * @param parent          parent folder
     */
    private void generateFolderChildren(NuxeoController nuxeoController, String basePath, FolderTask parent) throws PortletException {
        // Children
        SortedSet<FolderTask> children = this.getFolderChildren(nuxeoController, basePath, parent.getPath());
        parent.setChildren(children);

        if (CollectionUtils.isNotEmpty(children)) {
            for (FolderTask child : children) {
                // Children
                if (child.isSelected() || !child.isLazy()) {
                    this.generateFolderChildren(nuxeoController, basePath, child);
                }
            }
        }
    }


    @Override
    public SortedSet<FolderTask> getFolderChildren(PortalControllerContext portalControllerContext, String basePath, String path) throws PortletException {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        return this.getFolderChildren(nuxeoController, basePath, path);
    }


    /**
     * Get folder children.
     *
     * @param nuxeoController Nuxeo controller
     * @param basePath        base path
     * @param folderPath      folder path
     * @return folder children
     */
    private SortedSet<FolderTask> getFolderChildren(NuxeoController nuxeoController, String basePath, String folderPath) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = nuxeoController.getPortalCtx();

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Current navigation path
        String currentNavigationPath = nuxeoController.getNavigationPath();


        // Navigation items
        List<CMSItem> navigationItems;
        try {
            navigationItems = cmsService.getPortalNavigationSubitems(cmsContext, basePath, folderPath);
        } catch (CMSException e) {
            throw new PortletException(e);
        }


        // Children
        SortedSet<FolderTask> children;
        if (CollectionUtils.isEmpty(navigationItems)) {
            children = null;
        } else {
            children = new TreeSet<>(this.folderTaskComparator);

            for (CMSItem navigationItem : navigationItems) {
                FolderTask child = getFolderTask(portalControllerContext, nuxeoController, currentNavigationPath, navigationItem);

                children.add(child);
            }
        }

        return children;
    }


    /**
     * Check if path is a parent of current navigation path.
     *
     * @param path                  path
     * @param currentNavigationPath current navigation path
     * @return true if path is a parent of current navigation path
     */
    private boolean isSelected(String path, String currentNavigationPath) {
        return this.startsWith(currentNavigationPath, path);
    }


    /**
     * Check if path starts with prefix.
     *
     * @param testedPath   tested path
     * @param prefixedPath prefixed path
     * @return true if path starts with prefix
     */
    private boolean startsWith(String testedPath, String prefixedPath) {
        boolean result = StringUtils.startsWith(testedPath, prefixedPath);

        // "/parent/child-2/foo" starts with "/parent/child", but it isn't a child
        if (result) {
            String[] splittedTestedPath = StringUtils.split(testedPath, "/");
            String[] splittedPrefixedPath = StringUtils.split(prefixedPath, "/");

            int index = splittedPrefixedPath.length - 1;
            result = StringUtils.equals(splittedTestedPath[index], splittedPrefixedPath[index]);
        }

        return result;
    }


    /**
     * Get accepted types.
     *
     * @param item CMS item
     * @return accepted types
     */
    private String[] getAcceptedTypes(CMSItem item) {
        String[] acceptedTypes = null;
        if ((item != null) && (item.getType() != null)) {
            List<String> types = item.getType().getSubtypes();
            if (types != null) {
                acceptedTypes = types.toArray(new String[0]);
            }
        }
        return acceptedTypes;
    }


    @Override
    public List<Task> getSavedSearchesTasks(PortalControllerContext portalControllerContext, String activeSavedSearch) throws PortletException {
        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }
        // Saved searches
        List<UserSavedSearch> savedSearches = userPreferences.getSavedSearches();


        // Tasks
        List<Task> tasks;
        if (CollectionUtils.isEmpty(savedSearches)) {
            tasks = null;
        } else {
            tasks = new ArrayList<>(savedSearches.size());

            // Sort
            savedSearches.sort(this.savedSearchComparator);

            for (UserSavedSearch savedSearch : savedSearches) {
                // Task
                ServiceTask task = this.applicationContext.getBean(ServiceTask.class);
                task.setDisplayName(savedSearch.getDisplayName());
                task.setUrl(String.valueOf(savedSearch.getId()));
                task.setActive(StringUtils.equals(activeSavedSearch, String.valueOf(savedSearch.getId())));

                tasks.add(task);
            }
        }

        return tasks;
    }


    @Override
    public UserSavedSearch getSavedSearch(PortalControllerContext portalControllerContext, int id) throws PortletException {
        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // Saved searches
        List<UserSavedSearch> savedSearches = userPreferences.getSavedSearches();

        // Saved search
        UserSavedSearch savedSearch = null;
        if (CollectionUtils.isNotEmpty(savedSearches)) {
            Iterator<UserSavedSearch> iterator = savedSearches.iterator();
            while ((savedSearch == null) && iterator.hasNext()) {
                UserSavedSearch item = iterator.next();
                if (id == item.getId()) {
                    savedSearch = item;
                }
            }
        }

        return savedSearch;
    }


    @Override
    public void deleteSavedSearch(PortalControllerContext portalControllerContext, int id) throws PortletException {
        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // Saved searches
        List<UserSavedSearch> savedSearches = userPreferences.getSavedSearches();

        if (CollectionUtils.isNotEmpty(savedSearches)) {
            try {
                savedSearches.remove(this.userPreferencesService.createUserSavedSearch(portalControllerContext, id));
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        }

        userPreferences.setSavedSearches(savedSearches);
        userPreferences.setUpdated(true);
    }


    @Override
    public void moveDocuments(PortalControllerContext portalControllerContext, List<String> sourceIds, String targetId) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(MoveDocumentsCommand.class, sourceIds, targetId);
        nuxeoController.executeNuxeoCommand(command);
    }


    @Override
    public JSONArray loadVocabulary(PortalControllerContext portalControllerContext, String vocabularyName) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        nuxeoController.setCacheTimeOut(TimeUnit.HOURS.toMillis(1));
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT);

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(LoadVocabularyCommand.class, vocabularyName);

        return (JSONArray) nuxeoController.executeNuxeoCommand(command);
    }

}
