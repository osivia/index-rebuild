package fr.index.cloud.ens.search.saved.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonServiceImpl;
import fr.index.cloud.ens.search.saved.portlet.model.SavedSearchesForm;
import fr.index.cloud.ens.search.saved.portlet.model.SavedSearchesWindowProperties;
import fr.index.cloud.ens.search.saved.portlet.model.comparator.SavedSearchOrderComparator;
import fr.index.cloud.ens.search.saved.portlet.repository.SavedSearchesRepository;
import fr.toutatice.portail.cms.nuxeo.api.PageSelectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.page.PageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Saved searches portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonServiceImpl
 * @see SavedSearchesService
 */
@Service
public class SavedSearchesServiceImpl extends SearchCommonServiceImpl implements SavedSearchesService {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portlet repository.
     */
    @Autowired
    private SavedSearchesRepository repository;

    /**
     * Saved search order comparator.
     */
    @Autowired
    private SavedSearchOrderComparator savedSearchOrderComparator;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    
    /** Bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;
    
    /** Notifications service. */
    @Autowired
    private INotificationsService notificationsService;

    /**
     * Constructor.
     */
    public SavedSearchesServiceImpl() {
        super();
    }


    @Override
    public SavedSearchesWindowProperties getWindowProperties(PortalControllerContext portalControllerContext) {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        // Window properties
        SavedSearchesWindowProperties windowProperties = this.applicationContext.getBean(SavedSearchesWindowProperties.class);

        // Saved searches category identifier
        String categoryId = StringUtils.trimToEmpty(window.getProperty(CATEGORY_ID_WINDOW_PROPERTY));
        windowProperties.setCategoryId(categoryId);

        return windowProperties;
    }


    @Override
    public void setWindowProperties(PortalControllerContext portalControllerContext, SavedSearchesWindowProperties windowProperties) {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        // Saved searches category identifier
        window.setProperty(CATEGORY_ID_WINDOW_PROPERTY, StringUtils.trimToEmpty(windowProperties.getCategoryId()));
    }


    @Override
    public SavedSearchesForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        // Window properties
        SavedSearchesWindowProperties windowProperties = this.getWindowProperties(portalControllerContext);
        // Saved searches category identifier
        String categoryId = StringUtils.trimToEmpty(windowProperties.getCategoryId());

        // Form
        SavedSearchesForm form = this.applicationContext.getBean(SavedSearchesForm.class);

        // Saved searches
        List<UserSavedSearch> savedSearches = this.repository.getSavedSearches(portalControllerContext, categoryId);
        if (CollectionUtils.isNotEmpty(savedSearches)) {
            savedSearches.sort(this.savedSearchOrderComparator);
        }
        form.setSavedSearches(savedSearches);

        return form;
    }


    @Override
    public String renderView(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();

        // Form
        SavedSearchesForm form = this.getForm(portalControllerContext);

        // Empty response
        if (CollectionUtils.isEmpty(form.getSavedSearches())) {
            request.setAttribute("osivia.emptyResponse", "1");
        }

        return "view";
    }


    @Override
    public String getSavedSearchUrl(PortalControllerContext portalControllerContext, SavedSearchesForm form, int id) throws PortletException {
        // Navigation path
        String navigationPath = this.repository.getNavigationPath(portalControllerContext);

        // Saved searches
        List<UserSavedSearch> savedSearches = form.getSavedSearches();

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

        // URL
        String url;
        if (savedSearch == null) {
            url = null;
        } else {
            // Selectors
            String selectors = savedSearch.getData();
            // Location
            String location;
            // Page parameters
            Map<String, String> parameters = new HashMap<>(1);

            if (StringUtils.isEmpty(selectors)) {
                location = null;
            } else {
                List<String> locations = PageSelectors.decodeProperties(selectors).get(LOCATION_SELECTOR_ID);
                if (CollectionUtils.isEmpty(locations)) {
                    location = null;
                } else {
                    location = locations.get(0);
                }

                parameters.put(SELECTORS_PARAMETER, selectors);
                parameters.put(SEARCH_FILTER_PARAMETER, String.valueOf(id));
            }

            // Path
            String path;
            if (StringUtils.startsWith(navigationPath, MUTUALIZED_SPACE_PATH)) {
                path = MUTUALIZED_SPACE_PATH;
            } else if (StringUtils.isEmpty(location)) {
                path = this.repository.getUserWorkspacePath(portalControllerContext);
            } else {
                path = location;
            }

            // CMS URL
            url = this.portalUrlFactory.getCMSUrl(portalControllerContext, null, path, parameters, null, null, null, null,
                    null, null);
        }

        return url;
    }


    @Override
    public void deleteSavedSearch(PortalControllerContext portalControllerContext, int id) throws PortletException {
 
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

        // Window properties
        SavedSearchesWindowProperties windowProperties = this.getWindowProperties(portalControllerContext);
        // Saved searches category identifier
        String categoryId = StringUtils.trimToEmpty(windowProperties.getCategoryId());

        this.repository.deleteSavedSearch(portalControllerContext, categoryId, id);
        
        
        // Notification
        String message = bundle.getString("SEARCH_FILTER_MESSAGE_SUCCESS_DELETE_FILTER");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

        // Refresh other portlet model attributes
        PageProperties.getProperties().setRefreshingPage(true);
    }

}
