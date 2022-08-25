package fr.index.cloud.ens.application.management;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import fr.index.cloud.ens.application.api.IApplicationService;
import fr.index.cloud.ens.ws.DriveRestController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

/**
 * Applications management portlet service implementation.
 *
 * @author JS Steux
 * @see ApplicationManagementService
 * @see ApplicationContextAware
 */
@Service
public class ApplicationManagementServiceImpl implements ApplicationManagementService, ApplicationContextAware {

    private static final String PROP_TTC_WEBID = "ttc:webid";
    private static int MAX_RESULTS = 2;


    @Autowired
    IApplicationService etablissementService;
    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;
    /**
     * Notifications service.
     */
    @Autowired
    private INotificationsService notificationsService;
    /**
     * Application context.
     */
    private ApplicationContext applicationContext;


    /** View resolver. */
    @Autowired
    private InternalResourceViewResolver viewResolver;
    
    /** Dynamic page service. */
    @Autowired
    private IDynamicService dynamicService;


    /** Log. */
    private final Log log;


    /**
     * Constructor.
     */
    public ApplicationManagementServiceImpl() {
        super();
        this.log = LogFactory.getLog(this.getClass());
    }


    /**
     * Get search filters map.
     * 
     * @param portalControllerContext portal controller context
     * @param filters search filters
     * @return search filters map
     * @throws PortletException
     */
    protected Map<String, String> getFiltersMap(PortalControllerContext portalControllerContext, String filters) throws PortletException {
        Map<String, String> map;
        String[] arguments = StringUtils.split(filters, "&");
        if (ArrayUtils.isEmpty(arguments)) {
            map = null;
        } else {
            map = new HashMap<>(arguments.length);
            for (String argument : arguments) {
                String[] split = StringUtils.splitPreserveAllTokens(argument, "=");
                if (split.length == 2) {
                    try {
                        String key = URLDecoder.decode(split[0], CharEncoding.UTF_8);
                        String value = URLDecoder.decode(split[1], CharEncoding.UTF_8);
                        map.put(key, value);
                    } catch (UnsupportedEncodingException e) {
                        throw new PortletException(e);
                    }
                }
            }
        }

        return map;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ApplicationContextProvider.setApplicationContext(applicationContext);
    }


    private NuxeoController getNuxeoController() {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }

    @Override
    public ApplicationManagementForm getApplicationForm(PortalControllerContext portalControllerContext) throws PortletException {
        // application form
        ApplicationManagementForm form = this.applicationContext.getBean(ApplicationManagementForm.class);

        search(portalControllerContext, form);
        return form;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void search(PortalControllerContext portalControllerContext, ApplicationManagementForm form) throws PortletException {


        NuxeoController nuxeoController = getNuxeoController();

        Documents returnDocs = (Documents) nuxeoController.executeNuxeoCommand(new GetApplicationsByFilterCommand(form.getFilter(), MAX_RESULTS + 1));

        if (returnDocs.size() < MAX_RESULTS + 1) {

            List<Document> docs = new ArrayList<>();
            for (Document returnDoc : returnDocs) {
                docs.add(returnDoc);
            }

            form.setApplications(docs);
            form.setMaxResults(false);
        } else {
            form.setMaxResults(true);
            form.setApplications(new ArrayList<Document>());
        }


    }


    @Override
    public void search(PortalControllerContext portalControllerContext, ApplicationManagementForm form, String filters) throws PortletException {

        // Filters map
        Map<String, String> map = this.getFiltersMap(portalControllerContext, filters);

        // Update form
        String filter;
        if (MapUtils.isEmpty(map)) {
            filter = null;
        } else {
            filter = map.get("filter");
        }
        form.setFilter(filter);


        this.search(portalControllerContext, form);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void select(PortalControllerContext portalControllerContext, ApplicationManagementForm form) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Selected user identifier
        String id = form.getSelectedApplicationId();

        String applicationWebID = null;
        for (Document doc : form.getApplications()) {
            if (doc.getId().equals(id)) {
                applicationWebID = doc.getProperties().getString(PROP_TTC_WEBID);
            }
        }


        // Region
        String region = System.getProperty(REGION_PROPERTY);

        if (region == null) {
            this.log.error("Unable to start application card portlet: property '" + REGION_PROPERTY + "' is not defined.");
        } else if (StringUtils.isNotEmpty(applicationWebID)) {
            // Window properties
            Map<String, String> properties = new HashMap<>();
            properties.put("osivia.hideTitle", "1");
            properties.put("osivia.bootstrapPanelStyle", String.valueOf(true));
            properties.put(ThemeConstants.PORTAL_PROP_REGION, region);
            properties.put("applicationId", applicationWebID);
            properties.put("osivia.windowState","normal");
            properties.put("osivia.windowName","application-detail");

            try {
                dynamicService.startDynamicWindow(portalControllerContext, region, "index-cloud-portal-ens-application-cardInstance", properties);
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        }


    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveViewPath(PortalControllerContext portalControllerContext, String name) throws PortletException {
        // View
        View view;
        try {
            view = this.viewResolver.resolveViewName(name, null);
        } catch (Exception e) {
            throw new PortletException(e);
        }

        // Path
        String path;
        if ((view != null) && (view instanceof JstlView)) {
            JstlView jstlView = (JstlView) view;
            path = jstlView.getUrl();
        } else {
            path = null;
        }

        return path;
    }

}
