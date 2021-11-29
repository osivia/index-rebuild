package fr.index.cloud.ens.dashboard;

import fr.index.cloud.ens.application.api.Application;
import fr.index.cloud.ens.application.api.IApplicationService;
import fr.index.cloud.oauth.config.OAuth2ServerConfig;
import fr.index.cloud.oauth.tokenStore.AggregateRefreshTokenInfos;
import fr.index.cloud.oauth.tokenStore.IPortalTokenStore;
import fr.index.cloud.oauth.tokenStore.PortalRefreshToken;
import fr.index.cloud.oauth.tokenStore.PortalTokenStore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.portlet.*;
import java.util.*;

/**
 * Web service dashboard portlet service implementation.
 *
 * @author JS Steux
 * @see DashboardService
 * @see ApplicationContextAware
 */
@Service
public class DashboardServiceImpl implements DashboardService, ApplicationContextAware {


    @Autowired
    IApplicationService applicationService;
    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;
    /**
     * Notifications service.
     */
    @Autowired
    private INotificationsService notificationsService;
    /**
     * Application context.
     */
    private ApplicationContext applicationContext;
    @Autowired
    private IPortalTokenStore tokenStore;


    /**
     * Constructor.
     */
    public DashboardServiceImpl() {
        super();
    }

    @Bean
    public IPortalTokenStore tokenStore() {
        return new PortalTokenStore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dashboard getDashboard(PortalControllerContext portalControllerContext) throws PortletException {
        // Dashboard form
        Dashboard form = this.applicationContext.getBean(Dashboard.class);


        Collection<AggregateRefreshTokenInfos> tokens = tokenStore.findTokensByUserName(portalControllerContext.getRequest().getRemoteUser());

        Set<String> clientIds = new HashSet<String>();
        Date today = new Date();

        List<DashboardApplication> applications = new ArrayList<>();

        for (AggregateRefreshTokenInfos token : tokens) {
            String clientName = null;
            String clientId = token.getAuthentication().getClientId();


            if (token.getExpirationDate() == null || token.getExpirationDate().after(today)) {

                // One occurence per clientId
                if (!clientIds.contains(clientId)) {

                    Application oAuth2Application = applicationService.getApplicationByClientID(clientId);
                    if (oAuth2Application != null)
                        clientName = oAuth2Application.getTitle();

                    if (clientName == null) {
                        clientName = clientId;
                    }

                    // Application
                    DashboardApplication application = this.applicationContext.getBean(DashboardApplication.class);

                    application.setClientId(clientId);
                    application.setClientName(clientName);

                    applications.add(application);


                    clientIds.add(clientId);
                }
            }
        }

        form.setApplications(applications);


        return form;
    }


    /**
     * Update model.
     *
     * @param portalControllerContext portal controller context
     * @param form dashboard form
     * @param selection selected documents
     * @param bundle internationalization bundle
     * @param messagePrefix message prefix
     */
    private void updateModel(PortalControllerContext portalControllerContext, Dashboard dashboard, Bundle bundle, String messagePrefix)
            throws PortletException {


        // Notification
        String message = bundle.getString(messagePrefix + "SUCCESS");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);


        getDashboard(portalControllerContext);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ApplicationContextProvider.setApplicationContext(applicationContext);
    }

    @Override
    public void revoke(PortalControllerContext portalControllerContext, String revokedClientId, Dashboard dashboard) throws PortletException {
        // Internationalization bundle
        Locale locale = portalControllerContext.getRequest().getLocale();
        Bundle bundle = this.bundleFactory.getBundle(locale);


        Collection<AggregateRefreshTokenInfos> tokens = tokenStore.findTokensByUserName(portalControllerContext.getRequest().getRemoteUser());


        for (AggregateRefreshTokenInfos token : tokens) {

            String clientId = token.getAuthentication().getClientId();

            if (revokedClientId.equals(clientId)) {

                tokenStore.removeRefreshToken(new PortalRefreshToken(token.getValue()));

            }
        }


        // Update model
        this.updateModel(portalControllerContext, dashboard, bundle, "DASHBOARD_REVOKE_MESSAGE_");
        



    }

}
