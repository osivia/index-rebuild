/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.commands;

import java.util.Date;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * List all files in a user workspace
 * 
 * @author Loïc Billon
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExportWorkspaceCommand implements INuxeoCommand{
	

	
	public static final String FILTER_NOT_IN_TRASH = " AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != 'deleted'";
	
	private String userWorkspacePath;
	

	public ExportWorkspaceCommand(String userWorkspacePath) {
		this.userWorkspacePath = userWorkspacePath;

		
	}
	
	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand#execute(org.nuxeo.ecm.automation.client.Session)
	 */
	@Override
	public Object execute(Session nuxeoSession) throws Exception {
	
		// === Get paths for files and folders
        // Nuxeo request
        StringBuilder nuxeoRequest = new StringBuilder();

        nuxeoRequest.append(" ecm:primaryType IN ('File', 'Picture', 'Audio', 'Video', 'Folder') ");
        nuxeoRequest.append("AND ecm:path STARTSWITH '").append(this.userWorkspacePath).append("' ");
        nuxeoRequest.append(FILTER_NOT_IN_TRASH);
        nuxeoRequest.append(" ORDER BY ecm:path");

        // Operation request
        OperationRequest operationRequest = nuxeoSession.newRequest("Document.QueryES");
        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, "*");
        operationRequest.set("query", "SELECT * FROM Document WHERE " + nuxeoRequest);

        Documents documents = (Documents) operationRequest.execute();
        return documents;
		
	}

	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand#getId()
	 */
	@Override
	public String getId() {
		
		return this.getClass().getSimpleName() + "/"+userWorkspacePath+"/"+new Date().getTime();
	}

	
	
}
