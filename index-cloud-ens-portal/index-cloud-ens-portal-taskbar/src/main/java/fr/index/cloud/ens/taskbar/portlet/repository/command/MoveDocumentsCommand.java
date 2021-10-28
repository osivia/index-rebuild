package fr.index.cloud.ens.taskbar.portlet.repository.command;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.IdRef;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Move documents Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MoveDocumentsCommand implements INuxeoCommand {

    /**
     * Source identifiers.
     */
    private final List<String> sourceIds;
    /**
     * Target identifier.
     */
    private final String targetId;


    /**
     * Constructor.
     *
     * @param sourceIds source identifiers
     * @param targetId  target identifier
     */
    public MoveDocumentsCommand(List<String> sourceIds, String targetId) {
        super();
        this.sourceIds = sourceIds;
        this.targetId = targetId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Target parent document reference
        DocRef target = new IdRef(this.targetId);

        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        // Moved documents
        Documents documents = new Documents(this.sourceIds.size());

        for (String sourceId : this.sourceIds) {
            // Source document reference
            DocRef source = new IdRef(sourceId);

            Document document = documentService.move(source, target);
            documents.add(document);
        }

        return documents;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return null;
    }

}
