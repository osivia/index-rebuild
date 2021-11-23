package fr.index.cloud.ens.initializer.service.commands;

import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentSecurityService;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.DocumentPermissions;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.PathRef;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

public class CreateProcedureContainerCommand implements INuxeoCommand {

	private static final String DEFAULT_DOMAIN = "/default-domain";

	@Override
	public Object execute(Session nuxeoSession) throws Exception {
		
		DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);
		
		Document domain = documentService.getDocument(new PathRef(DEFAULT_DOMAIN));
		
		Documents containers = documentService.query("SELECT * FROM ProceduresContainer WHERE ecm:path STARTSWITH '"+DEFAULT_DOMAIN+"'");
		Document proceduresContainer;
		if(containers.size() < 1 ) {
			// Procedure container
			PropertyMap properties = new PropertyMap();
			properties.set("dc:title", "Procédures");
			proceduresContainer = documentService.createDocument(domain, "ProceduresContainer", "procedures", properties);
		}
		else {
			proceduresContainer = containers.get(0);
		}
		
		Documents modelsCtn = documentService.query("SELECT * FROM ProceduresModelsContainer WHERE ecm:path STARTSWITH '"+proceduresContainer.getPath() +"'");
		Document proceduresModelsContainer;
		if(modelsCtn.size() < 1 ) {
			PropertyMap properties = new PropertyMap();
			properties.set("dc:title", "Modèles");
			proceduresModelsContainer = documentService.createDocument(proceduresContainer, "ProceduresModelsContainer", "procedures-models", properties);
		}
		else {
			proceduresModelsContainer = modelsCtn.get(0);
		}

		Documents instancesCtn = documentService.query("SELECT * FROM ProceduresInstancesContainer WHERE ecm:path STARTSWITH '"+proceduresContainer.getPath()+"'");
		Document proceduresInstancesContainer;
		if(instancesCtn.size() < 1 ) {
			PropertyMap properties = new PropertyMap();
			properties.set("dc:title", "Instances");
			proceduresInstancesContainer = documentService.createDocument(proceduresContainer, "ProceduresInstancesContainer", "procedures-instances", properties);

			// ADD ACL
            DocumentSecurityService securityService = nuxeoSession.getAdapter(DocumentSecurityService.class);
            DocumentPermissions documentPermissions = new DocumentPermissions(1);
            documentPermissions.setPermission("Administrators", "Everything");
            securityService.addPermissions(proceduresInstancesContainer, documentPermissions, DocumentSecurityService.LOCAL_ACL, true);

		}
		else {
			proceduresInstancesContainer = instancesCtn.get(0);
		}
				
		return proceduresModelsContainer;
	}

	@Override
	public String getId() {
		return this.getClass().getSimpleName();
	}

}
