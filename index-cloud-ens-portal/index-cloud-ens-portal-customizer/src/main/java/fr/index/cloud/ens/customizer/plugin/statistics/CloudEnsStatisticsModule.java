package fr.index.cloud.ens.customizer.plugin.statistics;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.statistics.StatisticsModule;

import javax.portlet.PortletContext;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Statistics module.
 *
 * @author Cédric Krommenhoek
 * @see StatisticsModule
 */
public class CloudEnsStatisticsModule implements StatisticsModule {

    /**
     * Mutualized space path.
     */
    private static final String MUTUALIZED_SPACE_PATH = System.getProperty("config.mutualized.path");


    /**
     * Statistics session attribute.
     */
    private static final String STATISTICS_ATTRIBUTE = "cloud-ens.statistics";


    /**
     * Portlet context.
     */
    private final PortletContext portletContext;


    /**
     * Locks.
     */
    private final Map<String, ReentrantLock> locks;


    /**
     * Constructor.
     *
     * @param portletContext portlet context
     */
    public CloudEnsStatisticsModule(PortletContext portletContext) {
        super();
        this.portletContext = portletContext;

        // Locks
        this.locks = new ConcurrentHashMap<>();
    }


    @Override
    public void increments(PortalControllerContext portalControllerContext, String path) throws PortalException {
        if (StringUtils.startsWith(path, MUTUALIZED_SPACE_PATH) && !StringUtils.equals(path, MUTUALIZED_SPACE_PATH)) {
            // HTTP session
            HttpSession session = portalControllerContext.getHttpServletRequest().getSession();
            // Session attribute
            Object attribute = session.getAttribute(STATISTICS_ATTRIBUTE);

            // User statistics
            CloudEnsUserStatistics userStatistics;
            if (attribute instanceof CloudEnsUserStatistics) {
                userStatistics = (CloudEnsUserStatistics) attribute;
            } else {
                userStatistics = new CloudEnsUserStatistics();
                session.setAttribute(STATISTICS_ATTRIBUTE, userStatistics);
            }

            // Add document path
            userStatistics.getPaths().add(path);
        }
    }


    @Override
    public void update(PortalControllerContext portalControllerContext, HttpSession session) throws PortalException {
        // Session attribute
        Object attribute = session.getAttribute(STATISTICS_ATTRIBUTE);

        // User statistics
        CloudEnsUserStatistics userStatistics;
        if (attribute instanceof CloudEnsUserStatistics) {
            userStatistics = (CloudEnsUserStatistics) attribute;

            // Nuxeo controller
            NuxeoController nuxeoController = new NuxeoController(this.portletContext);
            nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
            nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);

            // Document paths
            Set<String> paths = userStatistics.getPaths();

            for (String path : paths) {
                // Nuxeo command
                INuxeoCommand command = new IncrementsDocumentViewsCommand(path);

                // Lock
                ReentrantLock lock = this.getLock(path);
                lock.lock();

                try {
                    nuxeoController.executeNuxeoCommand(command);
                } finally {
                    lock.unlock();
                }
            }
        }
    }


    /**
     * Get lock.
     *
     * @param path document path
     * @return lock
     */
    private synchronized ReentrantLock getLock(String path) {
        ReentrantLock lock = this.locks.get(path);

        if (lock == null) {
            lock = new ReentrantLock();
            this.locks.put(path, lock);
        }

        return lock;
    }

}
