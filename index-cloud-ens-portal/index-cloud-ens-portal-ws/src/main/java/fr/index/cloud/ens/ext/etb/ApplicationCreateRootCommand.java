package fr.index.cloud.ens.ext.etb;

import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Create applications root
 * 
 * 
 * @author Jean-Sébastien
 */
public class ApplicationCreateRootCommand implements INuxeoCommand {

    /** The applications folder title. */
    String APPLICATIONS_FOLDER_TITLE = "ApplicationsFolder";
    
    /** The applications folder type. */
    String APPLICATIONS_FOLDER_TYPE = "OAuth2ApplicationFolder";   
    
    
  
    public ApplicationCreateRootCommand() {
        super();
    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        String parentPath = EtablissementRepositoryImpl.getRootPath();
        parentPath = parentPath.substring(0, parentPath.lastIndexOf('/'));
        
        Document parent = documentService.getDocument(new DocRef(parentPath));
        
        PropertyMap properties = new PropertyMap();
        properties.set(EtablissementRepositoryImpl.TITLE_PROPERTY, APPLICATIONS_FOLDER_TITLE);

        // Creation
        Document document = documentService.createDocument(parent, APPLICATIONS_FOLDER_TYPE, EtablissementRepositoryImpl.APPLICATION_FOLDER_NAME, properties, true);

        return document;


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        return builder.toString();
    }
}
