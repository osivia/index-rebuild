package fr.index.cloud.ens.filebrowser.columns.configuration.portlet.configuration;

import fr.index.cloud.ens.directory.service.preferences.CustomizedUserPreferencesService;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.services.workspace.filebrowser.portlet.configuration.FileBrowserConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * File browser columns configuration portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.filebrowser.columns.configuration.portlet")
public class FileBrowserColumnsConfiguration extends FileBrowserConfiguration {

    /**
     * Constructor.
     */
    public FileBrowserColumnsConfiguration() {
        super();
    }


    @Override
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setCache(true);
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/columns-configuration/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }


    @Override
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = super.getMessageSource();
        messageSource.setBasenames("columns-configuration", "mutualized-file-browser", "customized-file-browser", "file-browser");
        return messageSource;
    }


    @Override
    public CustomizedUserPreferencesService getUserPreferencesService() {
        return DirServiceFactory.getService(CustomizedUserPreferencesService.class);
    }

}
