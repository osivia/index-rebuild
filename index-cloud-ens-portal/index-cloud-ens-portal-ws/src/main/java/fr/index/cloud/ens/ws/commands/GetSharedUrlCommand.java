package fr.index.cloud.ens.ws.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.cms.IDGenerator;

import fr.index.cloud.ens.ws.DriveRestController;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * Get shared url command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class GetSharedUrlCommand implements INuxeoCommand {


    /** Parent identifier. */
    private final Document doc;


    /** publication format */
    String format;
    
    /** The publish. */
    boolean publish;


    /**
     * Constructor.
     */
    public GetSharedUrlCommand(Document doc, String format, boolean publish) {
        super();
        this.doc = doc;
        this.format = format;
        this.publish = publish;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        PropertyMap properties = new PropertyMap();
        boolean removeFormat = false;

        String shareId = doc.getProperties().getString("rshr:linkId");
        if (StringUtils.isEmpty(shareId)) {
            shareId = IDGenerator.generateId();
            properties.set("rshr:linkId", shareId);
        }
        
        // The document must be visible
        if( publish) {
            Boolean enabledLink = doc.getProperties().getBoolean("rshr:enabledLink", false) ;
            if( !enabledLink)
                properties.set( "rshr:enabledLink", true);       
        }

        if (StringUtils.isNotEmpty(format))
            properties.set("rshr:format", format);
        
        if (DriveRestController.DEFAULT_FORMAT.equals(format)) {
            removeFormat = true;
        }

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Index.UpdateMetadata");
        request.setInput(doc);
        request.set("properties", properties);
        request.set("removeFormat", removeFormat);
        
        request.execute();

        // Return
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("shareId", shareId);
        return returnMap;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(" : ");
        builder.append(this.doc.getPath());

        return builder.toString();
    }

}
