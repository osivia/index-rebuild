package fr.index.cloud.ens.ext.etb;


import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.index.cloud.ens.application.api.Application;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Application delete command
 * 
 * @author Jean-Sébastien
 */
public class ApplicationDeleteCommand implements INuxeoCommand {

    private static final String PROP_TTC_WEBID = "ttc:webid";


    private Document doc;

    public ApplicationDeleteCommand(Document doc) {
        super();

        this.doc = doc;
    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        documentService.remove(doc);


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
        builder.append(this.doc.getPath());
        return builder.toString();
    }
}
