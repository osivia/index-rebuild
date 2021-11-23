/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.commands;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;

import fr.index.cloud.ens.directory.person.export.portlet.service.PersonExportService;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;

/**
 * List all export procedures
 * 
 * @author Loïc Billon
 *
 */
public class GetExportProceduresCommand implements INuxeoCommand {

	private String userId;

	public GetExportProceduresCommand(String userId) {
		this.userId = userId;
		
	}
	
	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand#execute(org.nuxeo.ecm.automation.client.Session)
	 */
	@Override
	public Object execute(Session nuxeoSession) throws Exception {
        // Clause
        StringBuilder clause = new StringBuilder();
        clause.append("ecm:primaryType = 'ProcedureInstance' ");

        clause.append("AND pi:procedureModelWebId = '").append(PersonExportService.MODEL_ID).append("' ");

        if (this.userId != null) {
            clause.append("AND pi:globalVariablesValues.").append("userId").append(" = '");
            clause.append(userId);
            clause.append("' ORDER BY dc:created DESC");
        }
	

        // Filtered clause
        String filteredClause = NuxeoQueryFilter.addPublicationFilter(NuxeoQueryFilterContext.CONTEXT_LIVE, clause.toString());

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.QueryES");
        request.set(Constants.HEADER_NX_SCHEMAS, "dublincore, procedureInstance");
        request.set("query", "SELECT * FROM Document WHERE " + filteredClause);

        return request.execute();
	}

	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand#getId()
	 */
	@Override
	public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getName());
        builder.append("|");
        builder.append(this.userId);
        return builder.toString();
	}

}
