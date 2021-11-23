package fr.index.cloud.ens.initializer.service.commands;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.*;

import java.io.File;
import java.net.URL;

import javax.portlet.PortletContext;

/**
 * Create publication space Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
public class CreatePublicationSpaceCommand implements INuxeoCommand {

    /**
     * Log.
     */
    private final Log log;

    /** The portlet context. */
    private PortletContext portletContext;
    /**
     * Constructor.
     */
    public CreatePublicationSpaceCommand(PortletContext portletContext) {
        super();
        this.portletContext = portletContext;
        // Log
        this.log = LogFactory.getLog(CreatePublicationSpaceCommand.class);
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);


        Document publicationSpace = null;

        try {
            // publication Space is NOT overwrited !!
            publicationSpace = documentService.getDocument(new PathRef("/default-domain/publication-space"));
        } catch (Exception e) {
            //
        }

        if (publicationSpace == null) {


            // Domain
            Document domain = documentService.getDocument(new PathRef("/default-domain"));

            // Publication space
            URL publicationSpaceUrl = portletContext.getResource("/WEB-INF/docs/publication-space/export-publication-space.zip");
            File publicationSpaceFile = new File(publicationSpaceUrl.getFile());
            Blob publicationSpaceBlob = new FileBlob(publicationSpaceFile);

            OperationRequest operationRequest = nuxeoSession.newRequest("FileManager.Import").setInput(publicationSpaceBlob);
            operationRequest.setContextProperty("currentDocument", domain);
            operationRequest.set("overwite", String.valueOf(true));
            operationRequest.execute();


            // Mass publication
            Documents documents = documentService
                    .query("SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/publication-space' AND ecm:primaryType <> 'PortalSite'");
            for (Document document : documents) {
                this.log.info("Publish document : " + document.getPath());

                operationRequest = nuxeoSession.newRequest("Document.SetOnLineOperation").setInput(document);
                operationRequest.execute();
            }
        }
        return null;
    }


    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

}
