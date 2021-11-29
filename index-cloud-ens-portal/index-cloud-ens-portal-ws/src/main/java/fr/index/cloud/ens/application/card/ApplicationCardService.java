package fr.index.cloud.ens.application.card;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;


public interface ApplicationCardService {
    
     /**
     * Get application card
     * 
     * @param portalControllerContext portal controller context
     * @return application card
     * @throws PortletException
     */
    ApplicationCard getApplicationCard(PortalControllerContext portalControllerContext) throws PortletException;


     
    /**
     * Save application card.
     * 
     * @param portalControllerContext portal controller context
     * @param form application edition form
     * @throws PortletException
     */
    void saveApplication(PortalControllerContext portalControllerContext,ApplicationCardForm form) throws PortletException;

    /**
     * Delete application.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void deleteApplication(PortalControllerContext portalControllerContext,ApplicationCardOptions options) throws PortletException;
   

    /**
     * Gets the options.
     *
     * @param portalControllerContext the portal controller context
     * @return the options
     * @throws PortletException the portlet exception
     */
    public ApplicationCardOptions getOptions(PortalControllerContext portalControllerContext) throws PortletException;

}
