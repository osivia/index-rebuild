package fr.index.cloud.ens.ext.etb;


import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.index.cloud.ens.application.api.Application;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Application modifications command
 * 
 * @author Jean-Sébastien
 */
public class ApplicationEditionCommand implements INuxeoCommand {

    private static final String PROP_TTC_WEBID = "ttc:webid";

    private Application application;
    private Document root;
    private Document doc;

    public ApplicationEditionCommand(Document root, Application application, Document doc) {
        super();
        this.application = application;
        this.root = root;
        this.doc = doc;
    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);


        if (doc == null) {
            // Type
            String type = EtablissementRepositoryImpl.APPLICATION_TYPE;

            // Properties
            PropertyMap properties = new PropertyMap();
            properties.set(EtablissementRepositoryImpl.TITLE_PROPERTY, application.getTitle());
            properties.set(PROP_TTC_WEBID, application.getCode());


            // Creation
            Document newDoc =  documentService.createDocument(root, type, null, properties, true);
            if( application.getDescription() != null)   {
                documentService.setProperty(newDoc, "dc:description", application.getDescription());               
            }
            
          } else {
            documentService.setProperty(doc, EtablissementRepositoryImpl.TITLE_PROPERTY, application.getTitle());
            
            if( application.getDescription() != null)   {
                documentService.setProperty(doc, "dc:description", application.getDescription());               
            }


        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("|");
        builder.append(this.application.getCode());
        return builder.toString();
    }
}
