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
 * Create configuration space Nuxeo command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class CreateConfigurationSpaceCommand implements INuxeoCommand {

    /**
     * Log.
     */
    private final Log log;


    private PortletContext portletContext;

    /**
     * Constructor.
     */
    public CreateConfigurationSpaceCommand(PortletContext portletContext) {
        super();
        this.portletContext = portletContext;
        // Log
        this.log = LogFactory.getLog(CreateConfigurationSpaceCommand.class);
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        Document configuration = null;
        try {

            configuration = documentService.getDocument(new PathRef("/default-domain/workspaces/configuration"));
        } catch (Exception e) {
            configuration = null;
            //
        }

        if (configuration == null) {


            // Domain
            Document domain = documentService.getDocument(new PathRef("/default-domain/workspaces"));

            // Publication space
            URL configurationSpaceUrl = portletContext.getResource("/WEB-INF/docs/configuration-space/export-configuration-space.zip");
            File configurationSpaceFile = new File(configurationSpaceUrl.getFile());
            Blob configurationSpaceBlob = new FileBlob(configurationSpaceFile);

            OperationRequest operationRequest = nuxeoSession.newRequest("FileManager.Import").setInput(configurationSpaceBlob);
            operationRequest.setContextProperty("currentDocument", domain);
            operationRequest.set("overwite", String.valueOf(true));
            operationRequest.execute();

        }

        return null;
    }


    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

}
