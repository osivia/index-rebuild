package fr.index.cloud.ens.ws;

import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.log.LogContext;
import org.osivia.portal.api.log.LogContextFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.status.IStatusService;
import org.osivia.portal.api.tokens.ITokenService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import fr.index.cloud.ens.ext.etb.BaseConfiguration;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;



/**
 * 
 * Acces to portal services
 * 
 * @author JS Steux
 *
 */
@Configuration
public class PortalServices  extends BaseConfiguration {



    /**
     * Get notifications service.
     * 
     * @return notification service
     */
    @Bean
    public INotificationsService getNotificationService() {
        return Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);
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
     * Get person service.
     *
     * @return person service
     */
    @Bean(name = "personUpdateService")
    // this bean doesn't support reload at application (ie non-portlet) level
    // -> to support hot deploy of custom-services.sar in non-portlet mode, need Prototype + proxy 
    @Scope(value="prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)  
    public PersonUpdateService getPersonService() {
    	return DirServiceFactory.getService(PersonUpdateService.class);
    }
    

    /**
     * Get token service
     * @return token service
     */
    @Bean
    public ITokenService getTokenService() {
        return Locator.findMBean(ITokenService.class, ITokenService.MBEAN_NAME);
    }

    
    /**
     * Get Logger context
     * @return logger context
     */
    @Bean
    public LogContext getLoggerContext() {
        return LogContextFactory.getLogContext();
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

}
