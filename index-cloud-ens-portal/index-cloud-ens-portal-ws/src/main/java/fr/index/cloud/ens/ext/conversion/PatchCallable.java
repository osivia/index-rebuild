package fr.index.cloud.ens.ext.conversion;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.web.IWebIdService;

import fr.index.cloud.ens.ws.GenericException;
import fr.index.cloud.ens.ws.commands.CommandUtils;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;

/**
 * The Patch thread.
 */
public class PatchCallable implements Callable<Integer> {

    /** The records. */
    List<PatchRecord> patchRecords;

    List<ConversionRecord> conversionRecords;

    NuxeoController controller;


    PortalControllerContext ctx;

    ConversionServiceImpl conversionService;

    /** portal Logger. */
    private static final Log logger = LogFactory.getLog(PatchCallable.class);


    public PatchCallable(ConversionServiceImpl conversionService, NuxeoController controller, List<PatchRecord> patchRecords,
            List<ConversionRecord> conversionRecords) {
        super();
        this.controller = controller;
        this.patchRecords = patchRecords;
        this.conversionRecords = conversionRecords;
        this.conversionService = conversionService;
    }


    @Override
    public Integer call() throws Exception {
        int nbRecords = 0;
        PortalControllerContext ctx = new PortalControllerContext(controller.getPortletCtx(), null, null);
        for (PatchRecord patchRecord : patchRecords) {

            String path = IWebIdService.FETCH_PATH_PREFIX + patchRecord.getDocId();

            try {
                Document currentDoc = controller.getDocumentContext(path).getDocument();

                String resultCode = conversionService.convertBatch(conversionRecords, patchRecord.getDocId(), patchRecord.getEtablissement(),
                        patchRecord.getField(), patchRecord.getPublishMetaData());

                String listName = null;

                if ("L".equals(patchRecord.getField()))
                    listName = "idxcl:levels";
                if ("S".equals(patchRecord.getField()))
                    listName = "idxcl:subjects";

                
                
                PatchCommand cmd = new PatchCommand(currentDoc, listName, resultCode, patchRecord.getResultCode());
                controller.executeNuxeoCommand(cmd);


            } catch (NuxeoException e) {
                logger.error("Erreur during patch for document " + patchRecord.getDocId(), e);
            }


            nbRecords++;
        }

        return nbRecords;
    }

}
