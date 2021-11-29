package fr.index.cloud.customizer;

import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.status.IStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import fr.index.cloud.ens.ext.etb.BaseConfiguration;

/**
 * Client detail customizer
 * 
 * @author Jean-Sébastien Steux
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.customizer,fr.index.cloud.ens.ext")
public class ClientDetailConfiguration extends BaseConfiguration {


    
    /**
     * Constructor.
     */
    public ClientDetailConfiguration() {
        super();
    }

    
    /**
     * Get cache service.
     * 
     * @return internationalization service
     */
    @Bean
    public ICacheService getCacheService() {
        return Locator.findMBean(ICacheService.class, ICacheService.MBEAN_NAME);
    }
    
    
    /**
     * Gets the status service.
     *
     * @return the status service
     */
    @Bean
    public IStatusService getStatusService() {
        return Locator.findMBean(IStatusService.class, "osivia:service=StatusServices");
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



}
