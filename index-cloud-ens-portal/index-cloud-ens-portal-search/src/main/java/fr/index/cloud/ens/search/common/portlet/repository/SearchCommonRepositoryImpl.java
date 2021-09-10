package fr.index.cloud.ens.search.common.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.command.LoadVocabularyCommand;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cms.VirtualNavigationUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.core.cms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.portlet.PortletException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Search common repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepository
 */
public abstract class SearchCommonRepositoryImpl implements SearchCommonRepository {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * CMS service locator.
     */
    @Autowired
    private ICMSServiceLocator cmsServiceLocator;

    /**
     * Taskbar service.
     */
    @Autowired
    private ITaskbarService taskbarService;

    /**
     * User preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;


    /**
     * Constructor.
     */
    public SearchCommonRepositoryImpl() {
        super();
    }


    @Override
    public String getNavigationPath(PortalControllerContext portalControllerContext) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        return nuxeoController.getNavigationPath();
    }


    @Override
    public NuxeoDocumentContext getDocumentContext(PortalControllerContext portalControllerContext, String path) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        return nuxeoController.getDocumentContext(path);
    }


    @Override
    public String getUserWorkspacePath(PortalControllerContext portalControllerContext) throws PortletException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // User workspace
        CMSItem userWorkspace;
        try {
            userWorkspace = cmsService.getUserWorkspace(cmsContext);
        } catch (CMSException e) {
            throw new PortletException(e);
        }

        // User workspace path
        String path;
        if (userWorkspace == null) {
            path = null;
        } else {
            path = userWorkspace.getCmsPath();
        }

        return path;
    }


    @Override
    public String getSearchPath(PortalControllerContext portalControllerContext) throws PortletException {
        return this.getTaskPath(portalControllerContext, ITaskbarService.SEARCH_TASK_ID);
    }


    /**
     * Get task path.
     *
     * @param portalControllerContext portal controller context
     * @param taskId                  task identifier
     * @return path
     */
    private String getTaskPath(PortalControllerContext portalControllerContext, String taskId) throws PortletException {
        // User workspace path
        String userWorkspacePath = this.getUserWorkspacePath(portalControllerContext);

        // Navigation tasks
        List<TaskbarTask> navigationTasks;
        if (StringUtils.isEmpty(userWorkspacePath)) {
            navigationTasks = null;
        } else {
            try {
                navigationTasks = this.taskbarService.getTasks(portalControllerContext, userWorkspacePath);
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        }

        // Search taskbar task
        TaskbarTask searchTask = null;
        if (CollectionUtils.isNotEmpty(navigationTasks)) {
            Iterator<TaskbarTask> iterator = navigationTasks.iterator();

            while ((searchTask == null) && iterator.hasNext()) {
                TaskbarTask task = iterator.next();

                // Task staple identifier
                String stapleId = VirtualNavigationUtils.getStapleId(task.getPath());

                if (StringUtils.equals(taskId, stapleId)) {
                    searchTask = task;
                }
            }
        }

        // Search path
        String path;
        if (searchTask == null) {
            path = null;
        } else {
            path = searchTask.getPath();
        }

        return path;
    }


    @Override
    public List<UserSavedSearch> getSavedSearches(PortalControllerContext portalControllerContext, String categoryId) throws PortletException {
        // User preferences
        UserPreferences userPreferences;
        try {
            userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        // Saved searches
        List<UserSavedSearch> savedSearches;
        if (userPreferences == null) {
            savedSearches = null;
        } else {
            savedSearches = userPreferences.getSavedSearches(categoryId);
        }

        return savedSearches;
    }


    @Override
    public JSONArray loadVocabulary(PortalControllerContext portalControllerContext, String vocabulary) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        nuxeoController.setCacheTimeOut(TimeUnit.HOURS.toMillis(1));
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT);

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(LoadVocabularyCommand.class, vocabulary);

        return (JSONArray) nuxeoController.executeNuxeoCommand(command);
    }

}
