package fr.index.cloud.ens.ext.conversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.PortletException;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cms.UpdateScope;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.stereotype.Repository;

import fr.index.cloud.ens.conversion.admin.ConversionAdminForm;
import fr.index.cloud.ens.conversion.admin.ConversionAdminService;
import fr.index.cloud.ens.ws.DriveRestController;
import fr.index.cloud.ens.ws.beans.MetadataClassifier;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

/**
 * The Class ConversionRepositoryImpl.
 */


@Repository
public class ConversionRepositoryImpl implements ConversionRepository {

    /** The name of configuration file */
    public static final String CONFIGURATION_FILE_NAME = "conversion-file";


    /** The last parsed digest. */
    String lastDigest = null;

    List<ConversionRecord> records = null;
    

    /**
     * Gets the nuxeo controller.
     *
     * @return the nuxeo controller
     */
    private NuxeoController getNuxeoController(boolean cache) {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        if (cache)
            nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT);
        else
            nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }

    @Override
    public Document getConfigurationDocument() throws PortletException {

       return getConfigurationDocument(true);
    }

    
    
    @Override
    public Document getConfigurationDocument(boolean cache) throws PortletException {

        Document config;
        try {
            config = getNuxeoController(cache).getDocumentContext(getConfigurationPath()).getDocument();
        } catch (NuxeoException e) {
            if (e.getErrorCode() == NuxeoException.ERROR_NOTFOUND) {
                config = null;
            } else
                throw new PortletException(e);
        }
        return config;
    }
    
    
    @Override   
    public void updateConfiguration(PortalControllerContext portalControllerContext, File file, String name, String contentType)  throws PortletException {
        // Update data
        NuxeoController nuxeoController = getNuxeoController( true);

        Document parent = nuxeoController.getDocumentContext(getConfigurationRootPath()).getDocument();

        Document config = getConfigurationDocument();

        FileBlob binary;
        if (file != null)
            binary = new FileBlob(file, name, contentType);
        else
            binary = null;

        config = (Document) nuxeoController.executeNuxeoCommand(new UpdateConfigurationCommand(parent, config, binary));

        
        // Notify CMS change 

        try {
            nuxeoController.notifyUpdate( config.getPath(), getConfigurationRootPath(), UpdateScope.SCOPE_SPACE, false);
        } catch (PortletException e) {
            throw new RuntimeException(e);
        }
        
   
        
    }
    

    public List<ConversionRecord> getRecords(PortalControllerContext ctx) throws PortletException {

        try {
            Document config;

            config = getConfigurationDocument();

            if (config != null) {
                PropertyMap map = config.getProperties().getMap(ConversionAdminService.XPATH);
                if (map != null && map.get("digest") != null) {
                    String digest = map.getString("digest");
                    if (!digest.equals(lastDigest)) {
                        loadRecords(ctx, config);
                        lastDigest = digest;
                    }
                }
            } else {
                this.records = null;
            }

        } catch (Exception e) {
            throw new PortletException(e);
        }

        return this.records;
    }


    /**
     * Load records from nuxeo document
     *
     * @param config the config
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws MalformedLineException the malformed exception
     */
    
    private void loadRecords(PortalControllerContext portalCtx, Document config) throws IOException, MalformedLineException {

        NuxeoController ctx = getNuxeoController(false);
        ctx.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        FileBlob fileBlob = null;
        fileBlob = (FileBlob) ctx.executeNuxeoCommand(new ConversionFileContentCommand(config));
        File file = fileBlob.getFile();

        List<ConversionRecord> records = extractRecordsFromFile(portalCtx, file);

        try {
            this.records = records;
        } finally {
            if (fileBlob != null)
                fileBlob.getFile().delete();

        }
    }

    /**
     * Extract records from file.
     *
     * @param file the file
     * @return the list
     * @throws FileNotFoundException the file not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws MalformedLineException the malformed exception
     */
    
    private List<ConversionRecord> extractRecordsFromFile(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException {

        FileInputStream fis = null;
        BufferedReader reader = null;


        List<ConversionRecord> records = new ArrayList<>();

        try {
           fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis));
            
            int iLine = 1;

            String line = reader.readLine();
            while (line != null) {

                String tokens[] = line.split(";", -1);
                if (tokens.length != 5) {
                    throw new MalformedLineException(iLine);
                }

                ConversionRecord record = new ConversionRecord();
                record.setField(tokens[0]);
                record.setEtablissement(tokens[1]);
                record.setPublishCode(tokens[2]);
                record.setPublishLabel(tokens[3]);
                record.setResultCode(tokens[4]);

                records.add(record);

                line = reader.readLine();
                iLine++;
            }
        } finally {
            if (reader != null)
                reader.close();
            if (fis != null)
                fis.close();

        }
        return records;
    }


    /**
     * Gets the configuration path.
     *
     * @return the configuration path
     */
    private String getConfigurationPath() {

        return getConfigurationRootPath() + "/" + CONFIGURATION_FILE_NAME;
    }

    private String getConfigurationRootPath() {
        return System.getProperty("config.rootPath");
    }

    @Override
    public List<ConversionRecord> checkFile(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException {
        
        List<ConversionRecord> records = extractRecordsFromFile(ctx, file);
        
        return records;

    }
    
    
    /**
     * Extract records from patch.
     *
     * @param file the file
     * @return the list
     * @throws FileNotFoundException the file not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws MalformedLineException the malformed exception
     */
    @Override
    public List<PatchRecord> extractRecordsFromPatch(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException {

        FileInputStream fis = null;
        BufferedReader reader = null;


        List<PatchRecord> records = new ArrayList<>();

        try {
           fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis));
            
            int iLine = 1;

            String line = reader.readLine();
            while (line != null) {

                String tokens[] = line.split(";", -1);
                if (tokens.length != 6) {
                    throw new MalformedLineException(iLine);
                }

                PatchRecord record = new PatchRecord();
                record.setDocId(tokens[0]);
                record.setEtablissement(tokens[1]);
                record.setField(tokens[2]);
                
                MetadataClassifier metadata = new MetadataClassifier();
                metadata.setCodes(convertToList(tokens[3]));
                metadata.setName(tokens[4]);
                record.setPublishMetaData(metadata);
                 
                record.setResultCode(tokens[5]);

                records.add(record);

                line = reader.readLine();
                iLine++;
            }
        } finally {
            if (reader != null)
                reader.close();
            if (fis != null)
                fis.close();

        }
        return records;
    }
    
    
    private List<String> convertToList(String codes){

        List<String> codesList = new ArrayList<>();
        String[] toks = codes.split(",");
        for(int i=0;i < toks.length; i++)   {
            codesList.add(toks[i]);
        }
        return codesList;
    }
}
