package fr.index.cloud.ens.customizer.plugin.cms;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;

/**
 * Get source document by webId Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
public class GetSourceByWebIdCommand implements INuxeoCommand {

    /**
     * Mutualized space path.
     */
    private static final String MUTUALIZED_SPACE_PATH = System.getProperty("config.mutualized.path");


    /**
     * Source document webId.
     */
    private final String sourceWebId;


    /**
     * Constructor.
     *
     * @param sourceWebId source document webId
     */
    public GetSourceByWebIdCommand(String sourceWebId) {
        super();
        this.sourceWebId = sourceWebId;
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Nuxeo request
        StringBuilder nuxeoRequest = new StringBuilder();
        nuxeoRequest.append("ecm:path STARTSWITH '").append(MUTUALIZED_SPACE_PATH).append("' ");
        nuxeoRequest.append("AND ttc:webid = '").append(this.sourceWebId).append("' ");

        // Query filter
        String filteredRequest = NuxeoQueryFilter.addPublicationFilter(NuxeoQueryFilterContext.CONTEXT_DEFAULT, nuxeoRequest.toString());

        // Operation request
        OperationRequest operationRequest = nuxeoSession.newRequest("Document.QueryES");
        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, "*");
        operationRequest.set("query", "SELECT * FROM Document WHERE " + filteredRequest);

        // Results
        return operationRequest.execute();
    }


    @Override
    public String getId() {
        return null;
    }

}
