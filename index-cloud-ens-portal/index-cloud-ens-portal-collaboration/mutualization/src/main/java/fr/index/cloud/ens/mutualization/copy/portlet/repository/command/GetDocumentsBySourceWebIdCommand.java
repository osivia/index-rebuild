package fr.index.cloud.ens.mutualization.copy.portlet.repository.command;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Get documents by source webId Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetDocumentsBySourceWebIdCommand implements INuxeoCommand {

    /**
     * Source document webId.
     */
    private final String sourceWebId;
    /**
     * Target document identifier.
     */
    private final String targetId;


    /**
     * Constructor.
     *
     * @param sourceWebId source document webId
     * @param targetId  target document identifier
     */
    public GetDocumentsBySourceWebIdCommand(String sourceWebId, String targetId) {
        super();
        this.sourceWebId = sourceWebId;
        this.targetId = targetId;
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Nuxeo request
        StringBuilder nuxeoRequest = new StringBuilder();
        nuxeoRequest.append("ecm:parentId = '").append(this.targetId).append("' ");
        nuxeoRequest.append("AND mtz:sourceWebId = '").append(this.sourceWebId).append("' ");
        nuxeoRequest.append("ORDER BY dc:title ASC");

        // Query filter
        String filteredRequest = NuxeoQueryFilter.addPublicationFilter(NuxeoQueryFilterContext.CONTEXT_LIVE, nuxeoRequest.toString());

        // Operation request
        OperationRequest operationRequest = nuxeoSession.newRequest("Document.QueryES");
        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, "common, dublincore");
        operationRequest.set("query", "SELECT * FROM Document WHERE " + filteredRequest);

        // Results
        return operationRequest.execute();
    }


    @Override
    public String getId() {
        return null;
    }

}
