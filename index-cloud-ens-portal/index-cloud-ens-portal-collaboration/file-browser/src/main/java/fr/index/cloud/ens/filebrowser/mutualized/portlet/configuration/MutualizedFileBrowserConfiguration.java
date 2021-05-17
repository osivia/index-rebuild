package fr.index.cloud.ens.filebrowser.mutualized.portlet.configuration;

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
 * Mutualized file browser portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.filebrowser.mutualized.portlet")
public class MutualizedFileBrowserConfiguration extends FileBrowserConfiguration {

    /**
     * Constructor.
     */
    public MutualizedFileBrowserConfiguration() {
        super();
    }


    /**
     * Get view resolver.
     *
     * @return view resolver
     */
    @Bean
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setCache(true);
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/mutualized/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }


    @Override
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = super.getMessageSource();
        messageSource.setBasenames("mutualized-file-browser", "customized-file-browser", "file-browser");
        return messageSource;
    }


    /**
     * Get user preferences service.
     *
     * @return user preferences service
     */
    @Bean
    public CustomizedUserPreferencesService getUserPreferencesService() {
        return DirServiceFactory.getService(CustomizedUserPreferencesService.class);
    }

}
