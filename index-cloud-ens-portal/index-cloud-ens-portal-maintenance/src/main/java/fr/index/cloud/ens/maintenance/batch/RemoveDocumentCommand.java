package fr.index.cloud.ens.maintenance.batch;


import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Remove document command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class RemoveDocumentCommand implements INuxeoCommand {


    private final Document procedureInstance;


    public RemoveDocumentCommand( Document procedureInstance) {
        this.procedureInstance = procedureInstance;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);
        documentService.remove(procedureInstance);
        
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getCanonicalName());
        return builder.toString();
    }

}
