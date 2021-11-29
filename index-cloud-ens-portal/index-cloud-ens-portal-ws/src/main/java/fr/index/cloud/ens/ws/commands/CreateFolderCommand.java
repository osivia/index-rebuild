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
 * CReate folder command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class CreateFolderCommand implements INuxeoCommand {


    /** Parent identifier. */
    private final Document parent;

    /** Parent string. */
    private final String folderName;

    /**
     * Constructor.
     */
    public CreateFolderCommand(Document parent, String folderName) {
        super();
        this.parent = parent;
        this.folderName = folderName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);
        
        PropertyMap properties = new PropertyMap();
        properties.set("dc:title", folderName);

        Document folder = documentService.createDocument(parent, "Folder", folderName, properties,true);

        return folder;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(" : ");
        builder.append(this.parent.getPath());

        return builder.toString();
    }

}
