package fr.index.cloud.ens.ext.conversion;

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


import fr.index.cloud.ens.ws.commands.CommandUtils;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Patch command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class PatchCommand implements INuxeoCommand {


    /** Parent identifier. */
    private final Document doc;


    /** meta-datas qualifier. */
    String listName;

    /** The new value. */
    String newValue;

    /** The old value. */
    String oldValue;


    /**
     * Constructor.
     *
     * @param doc the doc
     * @param qualifier the qualifier
     * @param newValue the new value
     * @param oldValue the old value
     */
    public PatchCommand(Document doc, String listName, String newValue, String oldValue) {
        super();
        this.doc = doc;
        this.listName = listName;
        this.newValue = newValue;
        this.oldValue = oldValue;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

 
        boolean isInserted = false;
        boolean isRemoved = false;
        
        PropertyList list = doc.getProperties().getList( listName);
        if( list == null)
            list = new PropertyList();
        

        
        // Remove old value
        if (StringUtils.isNotEmpty(oldValue) && !StringUtils.equals(newValue, oldValue))    
            isRemoved =  CommandUtils.removeFromList(list, oldValue);
        // Insert new value
        if (StringUtils.isNotEmpty(newValue))
            isInserted = CommandUtils.addToList(list, newValue);

        if( isInserted || isRemoved)    {
            
            PropertyMap properties = new PropertyMap();     
            properties.set(listName, CommandUtils.convertToString(list));
            
            // Operation request
            OperationRequest request = nuxeoSession.newRequest("Index.UpdateMetadata");
            request.setInput(doc);
            request.set("properties", properties);     
            
             
            request.execute();   
        }


        return null;
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
