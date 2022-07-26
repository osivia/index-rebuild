package fr.index.cloud.ens.conversion.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.spi.RepositorySelector;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.directory.v2.service.RoleService;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.index.cloud.ens.ext.conversion.ConversionRecord;
import fr.index.cloud.ens.ext.conversion.ConversionRepository;
import fr.index.cloud.ens.ext.conversion.IConversionService;
import fr.index.cloud.ens.ext.conversion.MalformedLineException;
import fr.index.cloud.ens.ext.conversion.PatchRecord;
import fr.index.cloud.ens.ext.conversion.UpdateConfigurationCommand;
import fr.index.cloud.ens.ws.DriveRestController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

/**
 * Conversion administration portlet service implementation.
 * 
 * @see ConversionAdminService
 */
@Service
public class ConversionAdminServiceImpl implements ConversionAdminService {

    private static final String ROLE_ADMIN = "role_admin";

    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;



    @Autowired
    INotificationsService notificationService;


    @Autowired
    ConversionRepository conversionRepository;


    @Autowired
    IConversionService conversionService;

    /** Internationalization bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;

    /**
     * Constructor.
     */
    public ConversionAdminServiceImpl() {
        super();
    }


    /**
     * Update model with new file
     *
     * @param portalControllerContext the portal controller context
     * @param form the form
     * @throws PortletException the portlet exception
     */
    void updateFormFromModel(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {

        Document config = conversionRepository.getConfigurationDocument();

        form.setFileDownloadUrl(null);

        if (config != null) {
            PropertyMap map = config.getProperties().getMap(ConversionAdminService.XPATH);
            if (map != null && map.get("data") != null) {
                NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
                // external cache, needs to reload
                form.setFileDownloadUrl(nuxeoController.getLink(config, "download").getUrl()+"&reload=true");
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ConversionAdminForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        ConversionAdminForm form = new ConversionAdminForm();
        form.setLogs(getLogs());
        updateFormFromModel(portalControllerContext, form);
        return form;
    }


    /**
     * Gets the nuxeo controller.
     *
     * @return the nuxeo controller
     */
    private NuxeoController getNuxeoController() {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ConversionAdminOptions getOptions(PortalControllerContext portalControllerContext) throws PortletException {

        ConversionAdminOptions options = this.applicationContext.getBean(ConversionAdminOptions.class);

        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);


        return options;
    }


    @Override
    public void uploadFile(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {

        // Temporary file
        MultipartFile upload = form.getFileUpload();
        File temporaryFile;
        try {
            temporaryFile = File.createTempFile("file-", ".tmp");
            upload.transferTo(temporaryFile);
            temporaryFile.deleteOnExit();

            form.setTemporaryFile(temporaryFile);

            // MIME type
            String contentType;
            try {
                contentType = new MimeType(upload.getContentType()).toString();
            } catch (MimeTypeParseException e) {
                contentType = null;
            }
            form.setContentType(contentType);
            form.setFileName(upload.getOriginalFilename());

            List<ConversionRecord> records = conversionRepository.checkFile(portalControllerContext, temporaryFile);

            form.setErrorMessage(null);
            Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
            form.setSuccessMessage(bundle.getString("CONVERSION_ADMIN_CORRECT_FORMAT", records.size()));

        } catch (MalformedLineException ex) {
            Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

            form.setErrorMessage(bundle.getString("CONVERSION_ADMIN_INCORRECT_LINEFORMAT", ex.getLine()));
            form.setSuccessMessage(null);
        } catch (IOException e) {
            throw new PortletException(e);
        }


    }


    @Override
    public void saveFile(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {
        updateConfiguration(portalControllerContext, form, form.getTemporaryFile(), form.getFileName(), form.getContentType());
        form.setSuccessMessage(null);
        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());  
        String message = bundle.getString("CONVERSION_ADMIN_NOTIF_FILE_CREATED_SUCCESS");        
        notificationService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);


    }

    @Override
    public void deleteFile(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {
        updateConfiguration(portalControllerContext, form, null, null, null);
        
        // Notification

        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());  
        String message = bundle.getString("CONVERSION_ADMIN_NOTIF_FILE_DELETED_SUCCESS");        
        notificationService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);
        
    }


    /**
     * Update configuration.
     *
     * @param portalControllerContext the portal controller context
     * @param file the file
     * @param name the name
     * @param contentType the content type
     * @throws PortletException the portlet exception
     */

    private void updateConfiguration(PortalControllerContext portalControllerContext, ConversionAdminForm form, File file, String name, String contentType)
            throws PortletException {

        // Update data
        conversionRepository.updateConfiguration(portalControllerContext, file, name, contentType);

        // update the model
        updateFormFromModel(portalControllerContext, form);
    }



    @Override
    public void applyPatch(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {


        try {
            conversionService.applyPatch(portalControllerContext, form.getPatchTemporaryFile());
        } catch (MalformedLineException | IOException e) {
            throw new PortletException(e);
        }

        form.setPatchSuccessMessage(null);
        
        // Notification

        Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());  
        String message = bundle.getString("CONVERSION_ADMIN_NOTIF_PATCH_SUCCESS");        
        notificationService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

    }

    @Override
    public void cancelPatch(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {
        
        if( form.getPatchTemporaryFile() != null)
            form.getPatchTemporaryFile().delete();
        
        form.setPatchTemporaryFile(null);
        form.setPatchFileName(null);
        form.setPatchContentType(null);      
        form.setPatchErrorMessage(null);
        form.setPatchSuccessMessage(null);
    }
    
    @Override
    public void cancelFile(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {
        
        if( form.getTemporaryFile() != null)
            form.getTemporaryFile().delete();
        
        form.setTemporaryFile(null);
        form.setFileName(null);
        form.setContentType(null);      
        form.setErrorMessage(null);
        form.setSuccessMessage(null);
    }


    @Override
    public void uploadPatch(PortalControllerContext portalControllerContext, ConversionAdminForm form) throws PortletException {

        // Temporary file
        MultipartFile upload = form.getPatchUpload();
        File temporaryFile;
        try {
            temporaryFile = File.createTempFile("file-", ".tmp");
            upload.transferTo(temporaryFile);
            temporaryFile.deleteOnExit();

            form.setPatchTemporaryFile(temporaryFile);

            // MIME type
            String contentType;
            try {
                contentType = new MimeType(upload.getContentType()).toString();
            } catch (MimeTypeParseException e) {
                contentType = null;
            }
            form.setPatchContentType(contentType);
            form.setPatchFileName(upload.getOriginalFilename());

            List<PatchRecord> records = conversionService.checkPatch(portalControllerContext, temporaryFile);

            form.setPatchErrorMessage(null);
            Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());
            form.setPatchSuccessMessage(bundle.getString("CONVERSION_ADMIN_PATCH_CORRECT_FORMAT", records.size()));

        } catch (MalformedLineException ex) {
            Bundle bundle = this.bundleFactory.getBundle(portalControllerContext.getRequest().getLocale());

            form.setPatchErrorMessage(bundle.getString("CONVERSION_ADMIN_PATCH_INCORRECT_LINEFORMAT", ex.getLine()));
            form.setPatchSuccessMessage(null);
        } catch (IOException e) {
            throw new PortletException(e);
        }


    }

    
    /**
     * Read last lines of e file
     *
     * @param file the file
     * @param numLastLineToRead the num last line to read
     * @return the list
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static List<String> readLastLine(File file, int numLastLineToRead) throws IOException {

        List<String> result = new ArrayList<>();
  
        ReversedLinesFileReader reader = new ReversedLinesFileReader(file);
        try {
            String line = "";
            while ((line = reader.readLine()) != null && result.size() < numLastLineToRead) {
                result.add(line);
            }
        } finally   {
            if(reader != null)
                reader.close();
        }


        return result;

    }
    


    /**
     * Gets the latest 100 lines of logs.
     *
     * @return the logs
     */
    public String getLogs() {

        StringBuffer result = new StringBuffer();
        try {
            
            List<String> orderedStrings = readLastLine(new File("/var/log/portal/portal_conversion.log"),100);
            
            
            result.append("<table class=\"table \">");
            
            for (String line : orderedStrings) {
                result.append("<tr>");
                if( line.endsWith(";"))
                    result.append("<tr  class=\"table-warning\">");
                else
                    result.append("<tr>");
                String tokens[] = line.split(";",-1);
                for(int i=0; i<tokens.length;i++)
                    result.append("<td>"+tokens[i]+"</td>");    
                result.append("</tr>");                
            }
            
            result.append("</table>");
        } catch (IOException e) {
            result.append("No log file found");
        }

        return result.toString();


    }


    @Override
    public void refreshLogs(PortalControllerContext portalControllerContext, ConversionAdminForm form) {
       form.setLogs(getLogs());
        
    }

}
