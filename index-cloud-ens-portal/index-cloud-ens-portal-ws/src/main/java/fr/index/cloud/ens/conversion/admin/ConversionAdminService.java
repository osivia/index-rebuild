package fr.index.cloud.ens.conversion.admin;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;


public interface ConversionAdminService {
    
    /** The name of configuration file */
    public static final String CONFIGURATION_FILE_NAME = "conversion-file";
    
    /** The xpath. of the confirguration file */
    public static String XPATH = "file:content";
   

    /**
     * Gets the options.
     *
     * @param portalControllerContext the portal controller context
     * @return the options
     * @throws PortletException the portlet exception
     */
    public ConversionAdminOptions getOptions(PortalControllerContext portalControllerContext) throws PortletException;

    
    /**
     * Upload file.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void uploadFile(PortalControllerContext portalControllerContext,ConversionAdminForm form) throws PortletException;
    
    
    

    /**
     * Save file.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    public void saveFile(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException ;
    
    
    
    /**
     * Delete file.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void deleteFile(PortalControllerContext portalControllerContext,ConversionAdminForm form) throws PortletException;



    /**
     * Gets the form.
     *
     * @param portalControllerContext the portal controller context
     * @return the form
     * @throws PortletException the portlet exception
     */
    ConversionAdminForm getForm(PortalControllerContext portalControllerContext) throws PortletException;
  
   
    
    /**
     * Upload patch.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
     void uploadPatch(PortalControllerContext portalControllerContext,ConversionAdminForm form) throws PortletException;
 
     
     
     
     /**
      * Apply patch.
      *
      * @param portalControllerContext the portal controller context
      * @param form the form
      * @throws PortletException the portlet exception
      */
     public void applyPatch(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException ;     
     
     
  

    /**
     * Refresh logs.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     */
    public void refreshLogs(PortalControllerContext portalControllerContext, ConversionAdminForm form);


    /**
     * Cancel patch.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void cancelPatch(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException;
     
    /**
     * Cancel file.
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void cancelFile(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException;

}
