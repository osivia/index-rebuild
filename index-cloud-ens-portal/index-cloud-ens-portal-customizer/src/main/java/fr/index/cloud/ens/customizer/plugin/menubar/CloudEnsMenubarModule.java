package fr.index.cloud.ens.customizer.plugin.menubar;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.MenubarContainer;
import org.osivia.portal.api.menubar.MenubarDropdown;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.menubar.MenubarModule;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;

import java.util.*;

/**
 * Menubar module.
 *
 * @see MenubarModule
 */
public class CloudEnsMenubarModule implements MenubarModule {

    /**
     * Mutualized space path.
     */
    private static final String MUTUALIZED_SPACE_PATH = System.getProperty("config.mutualized.path");

    /**
     * Document edition portlet instance.
     */
    private static final String DOCUMENT_EDITION_PORTLET_INSTANCE = "osivia-services-document-edition-instance";


    /**
     * Removed space items.
     */
    private final List<String> removedSpaceItems;
    /**
     * Removed document items.
     */
    private final List<String> removedDocumentItems;


    /**
     * Portal URL factory.
     */
    private final IPortalUrlFactory portalUrlFactory;
    /**
     * Internationalization bundle factory.
     */
    private final IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public CloudEnsMenubarModule() {
        super();

        // Removed space items
        this.removedSpaceItems = Arrays.asList("REFRESH", "PRINT");
        // Removed document items
        this.removedDocumentItems = Arrays.asList("WORKSPACE_ACL_MANAGEMENT", "SUBSCRIBE_URL", "VERSIONS");

        // Portal URL factory
        this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        // Internationalization bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    @Override
    public void customizeSpace(PortalControllerContext portalControllerContext, List<MenubarItem> menubar, DocumentContext spaceDocumentContext) {
        // Base path
        String basePath;
        if (spaceDocumentContext == null) {
            basePath = null;
        } else {
            basePath = spaceDocumentContext.getCmsPath();
        }

        if (StringUtils.equals(MUTUALIZED_SPACE_PATH, basePath)) {
            menubar.clear();
        } else {
            Iterator<MenubarItem> iterator = menubar.iterator();
            while (iterator.hasNext()) {
                MenubarItem item = iterator.next();
                MenubarContainer parent = item.getParent();

                if (this.removedSpaceItems.contains(item.getId())) {
                    iterator.remove();
                } else if (StringUtils.startsWith(basePath, "/default-domain/UserWorkspaces/") && (parent instanceof MenubarDropdown)) {
                    // Remove user workspace configuration dropdown
                    MenubarDropdown dropdown = (MenubarDropdown) parent;
                    if (StringUtils.equals("CONFIGURATION", dropdown.getId())) {
                        iterator.remove();
                    }
                }
            }
        }
    }


    @Override
    public void customizeDocument(PortalControllerContext portalControllerContext, List<MenubarItem> menubar, DocumentContext documentContext) throws PortalException {
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getHttpServletRequest().getLocale());


        // Path
        String path;
        if (documentContext == null) {
            path = null;
        } else {
            path = documentContext.getCmsPath();
        }

        if (StringUtils.startsWith(path, MUTUALIZED_SPACE_PATH)) {
            menubar.clear();
        } else if ((documentContext != null) && StringUtils.startsWith(path, "/default-domain/UserWorkspaces/")) {
            // Inside user workspace
            NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

            // Document type
            DocumentType documentType = documentContext.getDocumentType();

            // Removed menubar item identifiers
            List<String> itemIdentifiers = new ArrayList<>(this.removedDocumentItems);
            // Removed menubar dropdown menu identifiers
            List<String> dropdownIdentifiers = new ArrayList<>();

            if ((documentType != null) && documentType.isRoot()) {
                // Remove add item
                itemIdentifiers.add("ADD");
            } else {
                if (!documentContext.getPermissions().isAnonymouslyReadable()) {
                    // Remove share menu
                    dropdownIdentifiers.add("SHARE");
                }

                if (StringUtils.equals(nuxeoController.getBasePath(), StringUtils.substringBeforeLast(path, "/"))) {
                    // Remove delete, rename and move items
                    itemIdentifiers.add("DELETE");
                    itemIdentifiers.add("RENAME");
                    itemIdentifiers.add("MOVE");
                }
            }

            Iterator<MenubarItem> iterator = menubar.iterator();
            while (iterator.hasNext()) {
                MenubarItem item = iterator.next();
                MenubarContainer parent = item.getParent();

                if (itemIdentifiers.contains(item.getId())) {
                    iterator.remove();
                } else if ((documentType != null) && StringUtils.equals(item.getId(), "EDIT")) {
                    DocumentEditionType documentEditionType = DocumentEditionType.fromName(documentType.getName());
                    if ((documentEditionType != null) && documentEditionType.isEdition()) {
                        this.updateDocumentEditionMenubarItem(portalControllerContext, path, documentType.getName(), item, false, bundle);
                    } else {
                        item.setVisible(false);
                    }
                } else if (StringUtils.equals(item.getId(), "WORKSPACE_ADD") || StringUtils.startsWith(item.getId(), "ADD_") || StringUtils.startsWith(item.getId(), "NEW_")) {
                    item.setVisible(false);
                } else if (parent instanceof MenubarDropdown) {
                    MenubarDropdown dropdown = (MenubarDropdown) parent;
                    if (dropdownIdentifiers.contains(dropdown.getId())) {
                        iterator.remove();
                    }
                } else if ((documentType != null) && documentType.isFile()) {
                    item.setVisible(false);
                }
            }
        }
    }


    /**
     * Update document edition menubar item.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @param type                    document type
     * @param item                    menubar item
     * @param creation                creation indicator
     * @param bundle                  internationalization bundle
     */
    private void updateDocumentEditionMenubarItem(PortalControllerContext portalControllerContext, String path, String type, MenubarItem item, boolean creation, Bundle bundle) throws PortalException {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put("osivia.document.edition.base-path", nuxeoController.getBasePath());
        if (creation) {
            properties.put("osivia.document.edition.parent-path", path);
            properties.put("osivia.document.edition.document-type", type);
        } else {
            properties.put("osivia.document.edition.path", path);
        }
        properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, String.valueOf(true));
        properties.put("osivia.ajaxLink", "1");

        // URL
        String url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, DOCUMENT_EDITION_PORTLET_INSTANCE, properties, PortalUrlType.MODAL);

        // Title
        StringBuilder key = new StringBuilder();
        key.append("DOCUMENT_");
        if (creation) {
            key.append("CREATION_");
        } else {
            key.append("EDITION_");
        }
        key.append(StringUtils.upperCase(type));
        String title = bundle.getString(key.toString());

        // Update menubar item
        item.setUrl("javascript:");
        item.setOnclick(null);
        item.setHtmlClasses(null);
        item.getData().put("target", "#osivia-modal");
        item.getData().put("load-url", url);
        item.getData().put("title", title);
    }

}
