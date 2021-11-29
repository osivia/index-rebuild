package fr.index.cloud.ens.ext.conversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.portlet.PortletException;

import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.context.PortalControllerContext;


/**
 * The Interface Conversion Repository.
 */
public interface ConversionRepository {
    
    
    public Document getConfigurationDocument() throws PortletException;
    
    /**
     * Gets the records from the configuration file
     *
     * @param ctx the ctx
     * @return the records
     * @throws PortletException the portlet exception
     */
    public List<ConversionRecord> getRecords(PortalControllerContext ctx) throws PortletException;
    
    
    /**
     * Check if the format is correct
     *
     * @param ctx the ctx
     * @param file the file
     */
       
    public List<ConversionRecord> checkFile(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException;
    
    /**
     * Extract records from patch.
     *
     * @param ctx the ctx
     * @param file the file
     * @return the list
     * @throws FileNotFoundException the file not found exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws MalformedLineException the malformed line exception
     */
    List<PatchRecord> extractRecordsFromPatch(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException;

    /**
     * Update configuration.
     *
     * @param portalControllerContext the portal controller context
     * @param file the file
     * @param name the name
     * @param contentType the content type
     * @throws PortletException 
     */
    void updateConfiguration(PortalControllerContext portalControllerContext, File file, String name, String contentType) throws PortletException;

    /**
     * Gets the configuration document.
     *
     * @param cache the cache
     * @return the configuration document
     * @throws PortletException the portlet exception
     */
    Document getConfigurationDocument(boolean cache) throws PortletException;

}
