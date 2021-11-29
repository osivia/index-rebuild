package fr.index.cloud.ens.ext.etb;

import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.api.status.IStatusService;
import org.springframework.context.annotation.Bean;

/**
 * This class contains services needed by common services
 * 
 * 
 * The Class BasePortletConfiguration.
 */
public class BaseConfiguration {
    
    /**
     * Get cache service.
     * 
     * @return internationalization service
     */
    @Bean
    public ICacheService getCacheService() {
        return Locator.findMBean(ICacheService.class, ICacheService.MBEAN_NAME);
    }

    
    @Bean
    public IStatusService getStatusService() {
        return Locator.findMBean(IStatusService.class, "osivia:service=StatusServices");
    }
    
    @Bean
    public IProfilerService getProfilerService() {
        return Locator.findMBean(IProfilerService.class, "osivia:service=ProfilerService");
    }

    
}
