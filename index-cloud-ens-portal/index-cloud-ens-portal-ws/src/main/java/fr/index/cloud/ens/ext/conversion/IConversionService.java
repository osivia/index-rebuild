package fr.index.cloud.ens.ext.conversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;

import fr.index.cloud.ens.ws.beans.MetadataClassifier;

/**
 * Conversion from local property to BCN property
 * 
 * 
 */
public interface IConversionService {

    /**
     * Convert.
     *
     * @param ctx the ctx
     * @param clientId the client id
     * @param field the field
     * @param key the key
     * @param label the label
     * @return the string
     */

    String convert(PortalControllerContext ctx, String docId, String clientId, String field, MetadataClassifier metadata);
   
    /**
     * Check if the patch is correct
     *
     * @param ctx the ctx
     * @param file the file
     */
       
    public List<PatchRecord> checkPatch(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException;
   
    
    /**
     * Apply patch.
     *
     * @param ctx the ctx
     * @param file the file
     * @return the string
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws PortletException 
     */
    void applyPatch(PortalControllerContext ctx, File file)  throws  MalformedLineException, FileNotFoundException, IOException, PortletException;


}
