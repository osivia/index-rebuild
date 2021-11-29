package fr.index.cloud.ens.ws.commands;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * Upload files command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class UnpublishCommand implements INuxeoCommand {


    /** Parent identifier. */
    private final Document currentDoc;

    /** Parent string. */
    private final String unpubId;

    /**
     * Constructor.
     */
    public UnpublishCommand(Document currentDoc, String unpubId) {
        super();
        this.currentDoc = currentDoc;
        this.unpubId = unpubId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        int indice = -1;

        PropertyList targets = currentDoc.getProperties().getList("rshr:targets");
        for (int i = 0; i < targets.size(); i++) {
            PropertyMap target = targets.getMap(i);
            String pubId = target.getString("pubId");
            if (unpubId.equals(pubId)) {
                indice = i;
                break;
            }
        }


        if (indice == -1) {
            throw new RuntimeException("Publication with id '" + unpubId + " not consistent");
        }

        
        /* If document is not public, disable the link */
        PropertyMap properties = new PropertyMap();
        if (targets.size() == 1) {
            Boolean publicLink = currentDoc.getProperties().getBoolean("rshr:publicLink", false);
            if (!publicLink) {
                properties.set("rshr:enabledLink", false);
            }
        }      
        
        OperationRequest request = nuxeoSession.newRequest("Index.UpdateMetadata");
        request.setInput(currentDoc);
        request.set("targetIndex", indice);
        request.set("targetAction", "unpublish");      
        request.set("properties", properties);     
        
        request.execute();             
        



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
        builder.append(this.unpubId);

        return builder.toString();
    }

}
