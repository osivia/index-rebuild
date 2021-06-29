package fr.index.cloud.ens.mutualization.portlet.repository.command;

import fr.index.cloud.ens.mutualization.portlet.repository.MutualizationRepository;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.PathRef;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Disable mutualization Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DisableMutualizationCommand implements INuxeoCommand {

    /**
     * Document path.
     */
    private final String documentPath;
    /**
     * Mutualized space path.
     */
    private final String mutualizedSpacePath;

    /** Log. */
    private final Log log;


    /**
     * Constructor.
     *
     * @param documentPath        document path
     * @param mutualizedSpacePath mutualized space path
     */
    public DisableMutualizationCommand(String documentPath, String mutualizedSpacePath) {
        super();
        this.documentPath = documentPath;
        this.mutualizedSpacePath = mutualizedSpacePath;

        // Log
        this.log = LogFactory.getLog(this.getClass());
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        // Document
        Document document = documentService.getDocument(new PathRef(this.documentPath), "toutatice");
        // Document webId
        String webId = document.getString("ttc:webid");

        // Get proxy
        Document proxy = this.getProxy(nuxeoSession, webId);
        if (proxy == null) {
            this.log.error("Document mutualization is disabled but no proxy found (path = " + this.documentPath + ").");
        } else {
            documentService.remove(proxy);
        }

        // Updated properties
        PropertyMap properties = new PropertyMap();
        properties.set(MutualizationRepository.ENABLE_PROPERTY, false);

        return documentService.update(document, properties);
    }


    /**
     * Get document proxy.
     *
     * @param nuxeoSession Nuxeo session
     * @param webId        document webId
     * @return proxy, or null if not found
     */
    private Document getProxy(Session nuxeoSession, String webId) throws Exception {
        // Nuxeo request
        String nuxeoRequest = "ecm:path STARTSWITH '" + this.mutualizedSpacePath + "' AND ttc:webid = '" + webId + "'";

        // Operation request
        OperationRequest operationRequest = nuxeoSession.newRequest("Document.Query");
        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, "dublincore, common");
        operationRequest.set("query", "SELECT * FROM Document WHERE " + nuxeoRequest);

        // Operation results
        Documents results = (Documents) operationRequest.execute();

        // Proxy
        Document proxy;
        if ((results == null) || results.isEmpty()) {
            proxy = null;
        } else {
            proxy = results.get(0);
        }

        return proxy;
    }


    @Override
    public String getId() {
        return null;
    }

}
