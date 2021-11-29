
package fr.index.cloud.ens.ws.commands;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Document;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;

public class FolderGetChildrenCommand implements INuxeoCommand {
	
	Document folder;
	
	public FolderGetChildrenCommand(Document folder) {
		super();
		this.folder = folder;
	}
	
	public Object execute( Session session)	throws Exception {
        // Nuxeo request
        StringBuilder nuxeoRequest = new StringBuilder();
        nuxeoRequest.append("ecm:parentId = '").append(this.folder.getId()).append("' ");
        nuxeoRequest.append("AND ecm:primaryType != 'Workspace' ");
        nuxeoRequest.append("AND ecm:primaryType != 'WorkspaceRoot' ");
        nuxeoRequest.append("AND ecm:primaryType != 'PortalSite' ");
        nuxeoRequest.append("AND ecm:primaryType != 'Favorites' ");
        nuxeoRequest.append("ORDER BY ecm:pos ASC");

        // Query filter
        NuxeoQueryFilterContext queryFilterContext = new NuxeoQueryFilterContext(NuxeoQueryFilterContext.STATE_LIVE);
        String filteredRequest = NuxeoQueryFilter.addPublicationFilter(queryFilterContext, nuxeoRequest.toString());

        // Operation request
        OperationRequest operationRequest = session.newRequest("Document.QueryES");
        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, "dublincore, common, toutatice, file, ottcCheckined, resourceSharing");
        operationRequest.set("query", "SELECT * FROM Document WHERE " + filteredRequest);

        return operationRequest.execute();
	}

	public String getId() {
		return "FolderGetChildrenCommand/" + folder.getPath();
	};		

}
