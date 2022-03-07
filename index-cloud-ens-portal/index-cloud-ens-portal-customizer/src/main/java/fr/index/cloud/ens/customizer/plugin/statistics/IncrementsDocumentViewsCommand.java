package fr.index.cloud.ens.customizer.plugin.statistics;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.PathRef;

/**
 * Increments document views Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
public class IncrementsDocumentViewsCommand implements INuxeoCommand {

    /**
     * Update statistics operation identifier.
     */
    private static final String UPDATE_STATISTICS_OPERATION_ID = "Index.UpdateStatistics";


    /**
     * Document path.
     */
    private final String path;


    /**
     * Constructor.
     *
     * @param path document path
     */
    public IncrementsDocumentViewsCommand(String path) {
        super();
        this.path = path;
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Update statistics
        this.incrementsViews(nuxeoSession);

        return null;
    }


    /**
     * Increments views.
     *
     * @param nuxeoSession Nuxeo session
     */
    private void incrementsViews(Session nuxeoSession) throws Exception {
        // Document reference
        DocRef docRef = new PathRef(this.path);

        OperationRequest request = nuxeoSession.newRequest(UPDATE_STATISTICS_OPERATION_ID);
        request.setInput(docRef);
        request.set("incrementsViews", true);
        request.execute();
    }


    @Override
    public String getId() {
        return null;
    }

}
