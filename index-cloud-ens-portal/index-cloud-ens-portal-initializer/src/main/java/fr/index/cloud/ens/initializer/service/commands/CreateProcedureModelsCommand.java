package fr.index.cloud.ens.initializer.service.commands;

import java.io.File;
import java.net.URL;

import javax.portlet.PortletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

public class CreateProcedureModelsCommand implements INuxeoCommand {

    private Log logger = LogFactory.getLog(CreateProcedureModelsCommand.class);

    private Document modelsContainer;
    private PortletContext portletContext;

    public CreateProcedureModelsCommand(Document modelsContainer, PortletContext portletContext) {
        this.modelsContainer = modelsContainer;
        this.portletContext = portletContext;
    }

    private static final String DEFAULT_MODELS_PATH = "/default-domain/procedures/procedures-models";


    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);
        Documents models = documentService.query("SELECT * FROM ProcedureModel WHERE ecm:path STARTSWITH '" + DEFAULT_MODELS_PATH + "'");

        if (models.size() > 0) {
            return null;
        }


        URL proceduresUrl = portletContext.getResource("/WEB-INF/docs/models/");
        File dir = new File(proceduresUrl.getFile());
        File[] procedures = dir.listFiles();
        for (int i = 0; i < procedures.length; i++) {


            logger.info("Add procedure : " + procedures[i].getName());

            try {

                Blob blob = new FileBlob(procedures[i]);

                OperationRequest operationRequest = nuxeoSession.newRequest("FileManager.Import").setInput(blob);
                operationRequest.setContextProperty("currentDocument", this.modelsContainer.getId());
                operationRequest.set("overwite", "true");

                operationRequest.execute();
            } catch (Exception e) {
                logger.error("Error when importing procedure : " + procedures[i].getName());

            }
        }

        return null;

    }

    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

}
