package fr.index.cloud.ens.ws.commands;

import java.util.Date;
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


import fr.index.cloud.ens.ws.beans.PublishBean;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * Publish command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class PublishCommand implements INuxeoCommand {
    
    private final static String DEFAULT_FORMAT = "default";
    

    /** Parent identifier. */
    private final Document doc;

    

    /** organization */
    String organization;

    /** publication target */    
    PublishBean publishBean;
    
    /**
     * Constructor.
     */
    public PublishCommand(Document doc,  PublishBean publishBean, String organization) {
        super();
        this.doc = doc;
        this.organization= organization;


        this.publishBean = publishBean;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        PropertyMap properties = new PropertyMap();        
        Boolean enabledLink = doc.getProperties().getBoolean("rshr:enabledLink", false) ;
        if( !enabledLink)
            properties.set( "rshr:enabledLink", true);
         
        PropertyMap targetValue = new PropertyMap();
        String pubId = IDGenerator.generateId();
        targetValue.set("pubId", pubId);
        

        if( organization != null)
        targetValue.set("pubOrganization", organization);
        if( publishBean.getPubGroups() != null)
        targetValue.set("pubGroups", StringUtils.trimToNull(StringUtils.join(publishBean.getPubGroups(), ",")));  
        if( publishBean.getPubContext() != null)
            targetValue.set("pubContext",publishBean.getPubContext());
        if( publishBean.getSchoolYear() != null)
            targetValue.set("pubSchoolYear",publishBean.getSchoolYear());
        
        targetValue.set("pubDate", new Date(System.currentTimeMillis()));
        
         // Operation request
        OperationRequest request = nuxeoSession.newRequest("Index.UpdateMetadata");
        request.setInput(doc);
        request.set("targetValue", targetValue);
        request.set("targetAction", "publish");        
        request.set("properties", properties);     
        
         
        request.execute();        
        
        // Return 
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("pubId",pubId);
     
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
