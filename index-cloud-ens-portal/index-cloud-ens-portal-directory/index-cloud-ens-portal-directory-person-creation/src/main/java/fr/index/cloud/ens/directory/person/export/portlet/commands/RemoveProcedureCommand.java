/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.commands;

import java.util.Date;

import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * Remove a procedure
 * 
 * @author Loïc Billon
 *
 */
public class RemoveProcedureCommand implements INuxeoCommand {

	private Document procedureInstance;

	/**
	 * 
	 */
	public RemoveProcedureCommand(Document procedureInstance) {
		this.procedureInstance = procedureInstance;
	}
	
	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand#execute(org.nuxeo.ecm.automation.client.Session)
	 */
	@Override
	public Object execute(Session nuxeoSession) throws Exception {
		
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);
        documentService.remove(procedureInstance);
		
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand#getId()
	 */
	@Override
	public String getId() {
		return this.getClass().getSimpleName() + "/"+procedureInstance.getId()+"/"+new Date().getTime();
	}

}
