package fr.index.cloud.ens.customizer.plugin.cms;

import fr.index.cloud.ens.customizer.constants.CloudEnsConstants;
import fr.toutatice.portail.cms.nuxeo.api.domain.INavigationAdapterModule;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.IVirtualNavigationService;
import org.osivia.portal.api.cms.Symlink;
import org.osivia.portal.api.cms.Symlinks;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Customized navigation adapter.
 *
 * @author Cédric Krommenhoek
 * @see INavigationAdapterModule
 */
public class CloudEnsNavigationAdapter implements INavigationAdapterModule {

    /**
     * Mutualized space path.
     */
    private static final String MUTUALIZED_SPACE_PATH = System.getProperty("config.mutualized.path");


    /**
     * Log.
     */
    private final Log log;

    /**
     * CMS service locator.
     */
    private final ICMSServiceLocator cmsServiceLocator;
    /**
     * Virtual navigation service.
     */
    private final IVirtualNavigationService virtualNavigationService;
    /**
     * Internationalization bundle factory.
     */
    private final IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public CloudEnsNavigationAdapter() {
        super();

        // Log
        this.log = LogFactory.getLog(this.getClass());

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Virtual navigation service
        this.virtualNavigationService = Locator.findMBean(IVirtualNavigationService.class, IVirtualNavigationService.MBEAN_NAME);
        // Internationalization bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    @Override
    public String adaptNavigationPath(PortalControllerContext portalControllerContext, EcmDocument document) {
        return null;
    }


    @Override
    public Symlinks getSymlinks(PortalControllerContext portalControllerContext) {
        // Symlinks
        Symlinks symlinks = new Symlinks();

        // User workspace
        CMSItem userWorkspace = this.getUserWorkspace(portalControllerContext);

        if (userWorkspace != null) {
            symlinks = new Symlinks();
            List<Symlink> links = new ArrayList<>();
            symlinks.setLinks(links);

            // Recent items
            Symlink recentItemsSymlink = this.virtualNavigationService.createSymlink(userWorkspace.getCmsPath(), null, CloudEnsConstants.ROOT_WORKSPACE_PATH + "/recent-items", null);
            symlinks.getLinks().add(recentItemsSymlink);
        }

        symlinks.getPaths().add(CloudEnsConstants.ROOT_WORKSPACE_PATH);

        return symlinks;
    }


    @Override
    public void adaptNavigationItem(PortalControllerContext portalControllerContext, CMSItem navigationItem) {
        if ((navigationItem.getType() != null) && "Workspace".equals(navigationItem.getType().getName())) {
            // Virtual staples are not implemented in partial loading mode
            navigationItem.getProperties().put("partialLoading", "0");

            // User workspace
            CMSItem userWorkspace = this.getUserWorkspace(portalControllerContext);
            if ((userWorkspace != null) && StringUtils.equals(navigationItem.getCmsPath(), userWorkspace.getCmsPath())) {
                this.renameDocument(portalControllerContext, navigationItem, "TOOLBAR_USER_WORKSPACE");
            }
        } else if ((navigationItem.getType() != null) && "Folder".equals(navigationItem.getType().getName()) && StringUtils.endsWith(navigationItem.getCmsPath(), "/documents")) {
            // User workspace
            CMSItem userWorkspace = this.getUserWorkspace(portalControllerContext);
            if ((userWorkspace != null) && StringUtils.equals(navigationItem.getCmsPath(), userWorkspace.getCmsPath() + "/documents")) {
                this.renameDocument(portalControllerContext, navigationItem, "TOOLBAR_USER_WORKSPACE");
            }
        } else if ((navigationItem.getType() != null) && "PortalSite".equals(navigationItem.getType().getName()) && StringUtils.equals(MUTUALIZED_SPACE_PATH, StringUtils.removeEnd(navigationItem.getCmsPath(), ".proxy"))) {
            this.renameDocument(portalControllerContext, navigationItem, "TOOLBAR_COMMUNITY_WORKSPACE");
        }
    }


    /**
     * Rename document.
     *
     * @param portalControllerContext portal controller context
     * @param navigationItem          navigation item
     * @param titleKey                document title internationalization key
     */
    private void renameDocument(PortalControllerContext portalControllerContext, CMSItem navigationItem, String titleKey) {
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getHttpServletRequest().getLocale());

        // Title
        String title = bundle.getString(titleKey);

        // Update navigation item properties
        navigationItem.getProperties().put("displayName", title);
        navigationItem.getProperties().put("title", title);

        // Update navigation item document
        Document document = (Document) navigationItem.getNativeItem();
        document.set("dc:title", title);
    }


    /**
     * Get user workspace.
     *
     * @param portalControllerContext portal controller context
     * @return CMS item, may be null
     */
    private CMSItem getUserWorkspace(PortalControllerContext portalControllerContext) {
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
            userWorkspace = null;
            this.log.error("Unable to get user workspaces.", e);
        }
        return userWorkspace;
    }

}
