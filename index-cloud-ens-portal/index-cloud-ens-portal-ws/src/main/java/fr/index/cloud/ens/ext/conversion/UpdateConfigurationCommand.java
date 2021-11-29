package fr.index.cloud.ens.ext.conversion;

import fr.index.cloud.ens.conversion.admin.ConversionAdminService;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import org.apache.commons.collections.MapUtils;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PathRef;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import java.util.Map;

/**
 * Update configuration blob command
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class UpdateConfigurationCommand implements INuxeoCommand {

  
    
    /**
     * Document properties.
     */
    private Document parent;

    /**
     * Document properties.
     */
    private Document document;
    /**
     * Document binaries.
     */
    private  Blob binary;



    /**
     * Instantiates a new update configuration command.
     *
     * @param parent the parent
     * @param document the document (can be null)
     * @param binary the binary (can be null)
     */
    public UpdateConfigurationCommand(Document parent, Document document, Blob binary) {
        super();
        this.parent = parent;
        this.document=document;
        this.binary = binary;
    }


    @Override
    public String getId() {
        return this.getClass().getName() + "/" + System.currentTimeMillis();
    }



    @Override
    public Object execute(Session session) throws Exception {
         // Document service
        DocumentService documentService = session.getAdapter(DocumentService.class);
         
        // Document creation
        if( document == null && binary != null) {
            PropertyMap properties= new PropertyMap();
            properties.set("dc:title", "configuration"); 
            document = documentService.createDocument(parent, "File", ConversionAdminService.CONFIGURATION_FILE_NAME, properties);
        }

        if (binary == null) {
            // Remove blob           
            documentService.removeBlob(document, ConversionAdminService.XPATH);
        } else  {
            // add blob
            documentService.setBlob(document, binary, ConversionAdminService.XPATH);
        }
        
        return document;
    }



}
