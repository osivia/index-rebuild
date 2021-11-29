package fr.index.cloud.ens.dashboard;

import org.dom4j.Element;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import java.io.IOException;
import java.util.List;

/**
 * Dashboard service interface.
 *
 * @author JS Steux
 */
public interface DashboardService {

    /**
     * Get dashboard model.
     *
     * @param portalControllerContext portal controller context
     * @return dashboard form
     * @throws PortletException
     */
    
    public Dashboard getDashboard(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Revoke clientId
     *
     * @param portalControllerContext portal controller context
     * @param form                    trash form
     * @throws PortletException
     */
    public void revoke(PortalControllerContext portalControllerContext, String revokedClientId, Dashboard dashboard) throws PortletException;


}
