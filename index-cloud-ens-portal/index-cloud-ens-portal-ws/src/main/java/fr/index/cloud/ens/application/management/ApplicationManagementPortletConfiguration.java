/**
 * 
 */
package fr.index.cloud.ens.application.management;

import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.status.IStatusService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import fr.index.cloud.ens.ext.etb.BaseConfiguration;
import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoCustomizer;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;

/**
 * @author Jean-Sébastien Steux
 *
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.application.management,fr.index.cloud.ens.ext")

public class ApplicationManagementPortletConfiguration  extends BaseConfiguration {

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
        viewResolver.setPrefix("/WEB-INF/jsp/application/management/");
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
        return Locator.getService(ICMSServiceLocator.MBEAN_NAME, ICMSServiceLocator.class  );
    }


    /**
     * Get Nuxeo service.
     * 
     * @return Nuxeo service
     */
    @Bean
    public INuxeoService getNuxeoService() {
        return Locator.getService(INuxeoService.MBEAN_NAME, INuxeoService.class);
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
     * Get internationalization service.
     * 
     * @return internationalization service
     */
    @Bean
    public IInternationalizationService getInternationalizationService() {
        return Locator.getService(IInternationalizationService.class);
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
        return Locator.getService(INotificationsService.class);
    }
    
    /**
     * Get person service.
     *
     * @return person service
     */
    @Bean(name = "personUpdateService")
    public PersonUpdateService getPersonService() {
        return DirServiceFactory.getService(PersonUpdateService.class);
    }
    
    @Bean
    public IDynamicService getDynamicWindowService() {
        return Locator.getService(IDynamicService.class);
    }

    
}
