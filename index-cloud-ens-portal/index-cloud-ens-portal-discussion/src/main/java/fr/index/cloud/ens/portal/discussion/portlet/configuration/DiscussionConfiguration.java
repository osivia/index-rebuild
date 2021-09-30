package fr.index.cloud.ens.portal.discussion.portlet.configuration;

import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoCustomizer;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;

import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.tasks.ITasksService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Discussion portlet configuration.
 *
 * @author Jean-Sébastien Steux
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.portal.discussion.portlet")
public class DiscussionConfiguration {

    /**
     * Constructor.
     */
    public DiscussionConfiguration() {
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
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }


    /**
     * Get message source.
     *
     * @return message source
     */
    @Bean(name = "messageSource")
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("Resource");
        return messageSource;
    }


    /**
     * Get CMS service locator.
     *
     * @return CMS service locator
     */
    @Bean
    public ICMSServiceLocator getCmsServiceLocator() {
        return Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
    }


    /**
     * Get Nuxeo service.
     *
     * @return Nuxeo service
     */
    @Bean
    public INuxeoService getNuxeoService() {
        return Locator.findMBean(INuxeoService.class, INuxeoService.MBEAN_NAME);
    }


    /**
     * Get Nuxeo customizer.
     *
     * @param nuxeoService Nuxeo service
     * @return Nuxeo customizer
     */
    @Bean
    public INuxeoCustomizer getNuxeoCustomizer(INuxeoService nuxeoService) {
        return nuxeoService.getCMSCustomizer();
    }


    /**
     * Get portal URL factory.
     *
     * @return portal URL factory
     */
    @Bean
    public IPortalUrlFactory getPortalUrlFactory() {
        return Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
    }


    /**
     * Get internationalization service.
     *
     * @return internationalization service
     */
    @Bean
    public IInternationalizationService getInternationalizationService() {
        return Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
    }


    /**
     * Get internationalization bundle factory.
     *
     * @param internationalizationService internationalization service
     * @return internationalization bundle factory
     */
    @Bean
    public IBundleFactory getBundleFactory(IInternationalizationService internationalizationService) {
        return internationalizationService.getBundleFactory(this.getClass().getClassLoader());
    }


    /**
     * Get notifications service.
     *
     * @return notifications service
     */
    @Bean
    public INotificationsService getNotificationsService() {
        return Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);
    }


    /**
     * Get document DAO.
     *
     * @return document DAO
     */
    @Bean
    public DocumentDAO getDocumentDao() {
        return DocumentDAO.getInstance();
    }


    /**
     * Get person service.
     *
     * @return person service
     */
    @Bean
    public PersonService getPersonService() {
        return DirServiceFactory.getService(PersonService.class);
    }

    
    /**
     * Get tasks service.
     * 
     * @return tasks service
     */
    @Bean
    public ITasksService getTasksService() {
        return Locator.findMBean(ITasksService.class, ITasksService.MBEAN_NAME);
    }

    
    /**
     * Get user preferences service.
     *
     * @return user preferences service
     */
    @Bean
    public UserPreferencesService getUserPreferencesService() {
        return DirServiceFactory.getService(UserPreferencesService.class);
    }

    
}


