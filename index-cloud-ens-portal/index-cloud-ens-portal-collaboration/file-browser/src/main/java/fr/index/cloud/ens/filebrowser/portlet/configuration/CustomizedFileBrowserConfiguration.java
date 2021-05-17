package fr.index.cloud.ens.filebrowser.portlet.configuration;

import fr.index.cloud.ens.directory.service.preferences.CustomizedUserPreferencesService;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.services.workspace.filebrowser.portlet.configuration.FileBrowserConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * File browser customized portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.filebrowser.portlet")
public class CustomizedFileBrowserConfiguration extends FileBrowserConfiguration {

    /**
     * Constructor.
     */
    public CustomizedFileBrowserConfiguration() {
        super();
    }


    @Override
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = super.getMessageSource();
        messageSource.setBasenames("customized-file-browser", "file-browser");
        return messageSource;
    }


    @Override
    public CustomizedUserPreferencesService getUserPreferencesService() {
        return DirServiceFactory.getService(CustomizedUserPreferencesService.class);
    }

}
