package fr.index.cloud.ens.customizer.plugin;

import fr.index.cloud.ens.customizer.plugin.avatar.CloudEnsAvatarModule;
import fr.index.cloud.ens.customizer.plugin.cms.CloudEnsNavigationAdapter;
import fr.index.cloud.ens.customizer.plugin.cms.FileDocumentModule;
import fr.index.cloud.ens.customizer.plugin.cms.MutualizationSpaceSummaryListModule;
import fr.index.cloud.ens.customizer.plugin.menubar.CloudEnsMenubarModule;
import fr.index.cloud.ens.customizer.plugin.tasks.CloudEnsTaskModule;
import fr.index.cloud.ens.customizer.plugin.theming.CloudEnsTemplateAdapter;
import fr.toutatice.portail.cms.nuxeo.api.avatar.AvatarModule;
import fr.toutatice.portail.cms.nuxeo.api.domain.AbstractPluginPortlet;
import fr.toutatice.portail.cms.nuxeo.api.domain.INavigationAdapterModule;
import fr.toutatice.portail.cms.nuxeo.api.domain.ListTemplate;
import fr.toutatice.portail.cms.nuxeo.api.portlet.IPortletModule;

import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.MenubarModule;

import org.osivia.portal.api.taskbar.TaskbarFactory;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItems;
import org.osivia.portal.api.tasks.TaskModule;
import org.osivia.portal.api.theming.TemplateAdapter;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Demo plugin.
 *
 * @author Cédric Krommenhoek
 * @see AbstractPluginPortlet
 */
public class CloudEnsPlugin extends AbstractPluginPortlet {

    /**
     * Plugin name.
     */
    private static final String PLUGIN_NAME = "cloud-ens.plugin";


    /**
     * Internationalization bundle factory.
     */
    private final IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public CloudEnsPlugin() {
        super();

        // Internationalization bundle factory
        IInternationalizationService internationalizationService = Locator.getService(IInternationalizationService.class);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws PortletException {
        super.init();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPluginName() {
        return PLUGIN_NAME;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void customizeCMSProperties(CustomizationContext customizationContext) {
        // Menubar modules
        this.customizeMenubarModules(customizationContext);
        // List templates
        this.customizeListTemplates(customizationContext);
        // Template adapters
        this.customizeTemplateAdapters(customizationContext);
        // Taskbar items
        this.customizeTaskbarItems(customizationContext);        
        // Navigation adapters
        this.customizeNavigationAdapters(customizationContext);
        // Document modules
        this.customizeDocumentModules(customizationContext);
    }


    /**
     * Customize taskbar items.
     *
     * @param customizationContext customization context
     */
    private void customizeTaskbarItems(CustomizationContext customizationContext) {
        // Taskbar items
        TaskbarItems items = this.getTaskbarItems(customizationContext);
        // Factory
        TaskbarFactory factory = this.getTaskbarService().getFactory();

        // Recent items
        TaskbarItem recentItems = factory.createStapledTaskbarItem("RECENT_ITEMS", "RECENT_ITEMS_TASK", "glyphicons glyphicons-basic-history", new UniversalID( "idx", "DEFAULT_TEMPLATES_RECENT"));
        items.add(recentItems);

        // Search filters
        TaskbarItem searchFilters = factory.createStapledTaskbarItem("SEARCH_FILTERS", "SEARCH_FILTERS_TASK", "glyphicons glyphicons-basic-search",new UniversalID( "idx", "DEFAULT_TEMPLATES_SEARCH-FILTERS") );
        items.add(searchFilters);
    }

    
    
    
    /**
     * Customize navigation adapters.
     *
     * @param customizationContext customization context
     */
    private void customizeNavigationAdapters(CustomizationContext customizationContext) {
        // Navigation adapters
        List<INavigationAdapterModule> navigationAdapters = this.getNavigationAdapters(customizationContext);

        // Customized navigation adapter
        INavigationAdapterModule navigationAdapter = new CloudEnsNavigationAdapter();
        navigationAdapters.add(navigationAdapter);
    }
    
    /**
     * Customize menubar modules.
     *
     * @param customizationContext customization context
     */
    private void customizeMenubarModules(CustomizationContext customizationContext) {
        // Menubar modules
        List<MenubarModule> modules = this.getMenubarModules(customizationContext);

        // Menubar module
        MenubarModule module = new CloudEnsMenubarModule();
        modules.add(module);
    }


    /**
     * Customize list templates.
     *
     * @param customizationContext customization context
     */
    private void customizeListTemplates(CustomizationContext customizationContext) {
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(customizationContext.getLocale());

        // List templates
        Map<String, ListTemplate> templates = this.getListTemplates(customizationContext);

        // Quick access
        ListTemplate quickAccessList = new ListTemplate("quick-access", bundle.getString("LIST_TEMPLATE_QUICK_ACCESS"), "*");
        templates.put(quickAccessList.getKey(), quickAccessList);

        // Mutualization space summary
        ListTemplate mutualizationSpaceSummaryList = new ListTemplate("mutualization-space-summary", bundle.getString("LIST_TEMPLATE_MUTUALIZATION_SPACE_SUMMARY"), "*");
        mutualizationSpaceSummaryList.setModule(new MutualizationSpaceSummaryListModule(this.getPortletContext()));
        templates.put(mutualizationSpaceSummaryList.getKey(), mutualizationSpaceSummaryList);

        // My publications
        ListTemplate myPublicationsList = new ListTemplate("my-publications", bundle.getString("LIST_TEMPLATE_MY_PUBLICATIONS"), "*");
        templates.put(myPublicationsList.getKey(), myPublicationsList);
    }


    /**
     * Customize template adapters.
     *
     * @param customizationContext customization context
     */
    private void customizeTemplateAdapters(CustomizationContext customizationContext) {
        // Template adapters
        List<TemplateAdapter> adapters = this.getTemplateAdapters(customizationContext);

        // Template adapter
        TemplateAdapter adapter = new CloudEnsTemplateAdapter();
        adapters.add(adapter);
    }




    /**
     * Customize document modules.
     *
     * @param customizationContext customization context
     */
    private void customizeDocumentModules(CustomizationContext customizationContext) {
        // Portlet context
        PortletContext portletContext = this.getPortletContext();

        // File document module
        IPortletModule fileDocumentModule = new FileDocumentModule(portletContext);

        for (String type : Arrays.asList("File", "Picture", "Audio", "Video")) {
            // Document modules
            List<IPortletModule> modules = this.getDocumentModules(customizationContext, type);

            modules.add(fileDocumentModule);
        }
    }





}
