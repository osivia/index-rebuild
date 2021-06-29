package fr.index.cloud.ens.mutualization.copy.portlet.repository.command;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.FileBlob;

/**
 * Mutualization copy Nuxeo abstract super-class.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
public abstract class MutualizationCopyCommand implements INuxeoCommand {

    /**
     * Update statistics operation identifier.
     */
    private static final String UPDATE_STATISTICS_OPERATION_ID = "Index.UpdateStatistics";


    /**
     * Source document.
     */
    private final Document source;


    /**
     * Constructor.
     *
     * @param source source document
     */
    public MutualizationCopyCommand(Document source) {
        super();
        this.source = source;
    }


    @Override
    public String getId() {
        return null;
    }


    /**
     * Copy file BLOB.
     *
     * @param documentService document service
     * @param target          target document
     */
    protected void copyBlob(DocumentService documentService, DocRef target) throws Exception {
        FileBlob blob = documentService.getBlob(this.source);
        documentService.setBlob(target, blob);
    }


    /**
     * Update statistics.
     *
     * @param nuxeoSession Nuxeo session
     */
    protected void updateStatistics(Session nuxeoSession) throws Exception {
        OperationRequest request = nuxeoSession.newRequest(UPDATE_STATISTICS_OPERATION_ID);
        request.setInput(this.source);
        request.set("incrementsDownloads", true);
        request.execute();
    }


    public Document getSource() {
        return source;
    }

}
