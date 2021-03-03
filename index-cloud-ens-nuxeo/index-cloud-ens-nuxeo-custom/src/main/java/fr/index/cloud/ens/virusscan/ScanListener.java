/**
 * 
 */
package fr.index.cloud.ens.virusscan;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;


/**
 * Scan new files thanks to ICAP compatible scanner
 * 
 * @author jean-sébastien steux
 *
 */
public class ScanListener implements EventListener {

    /** Log. */
    private static final Log log = LogFactory.getLog(ScanListener.class);

    /** Dont' modify. Used at portal level (NuxeoException) */
    private static String DEFAULT_ERROR_VIRUS_FOUND_MESSAGE = "Virus found";

    private static String ERROR_VIRUS_FOUND_LOCALIZED_MESSAGE = "label.error.index.custom.virusFound";

    /** The timeout for scan */
    public static final long SCAN_TIMEOUT = 10;


    @Override
    public void handleEvent(Event event) throws ClientException {

        if (log.isDebugEnabled())
            log.debug("ScanListener.handleEvent " + event.getName());

        if ((event.getContext() instanceof DocumentEventContext) && (DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName()))) {

            DocumentEventContext evtCtx = (DocumentEventContext) event.getContext();
            DocumentModel docToUpdate = evtCtx.getSourceDocument();
            if (docToUpdate.hasSchema(ToutaticeNuxeoStudioConst.CST_DOC_FILE_SCHEMA)) {
                if (docToUpdate != null) {
                    DataModel dm = docToUpdate.getDataModel("file");
                    if (dm != null && dm.isDirty()) {
                        // Blob has been modified
                        checkFile(event, docToUpdate);
                    }
                }
            }
        }


        if ((event.getContext() instanceof DocumentEventContext) && (DocumentEventTypes.ABOUT_TO_CREATE.equals(event.getName()))
                || (DocumentEventTypes.ABOUT_TO_IMPORT.equals(event.getName()))) {

            DocumentEventContext evtCtx = (DocumentEventContext) event.getContext();
            DocumentModel docToCreate = evtCtx.getSourceDocument();
            if (docToCreate.hasSchema(ToutaticeNuxeoStudioConst.CST_DOC_FILE_SCHEMA)) {
                checkFile(event, docToCreate);
            }
        }

    }


    /**
     * check the file and throws a VirusScanException if a virus is detected
     * 
     * 
     * @param event
     * @param docToCreate
     */
    private void checkFile(Event event, DocumentModel docToScan) {

        ScanResult res = ScanChecker.getInstance().checkFile(docToScan, event.getContext().getCoreSession(), false, SCAN_TIMEOUT);

        if (res.getErrorCode() == ScanResult.ERROR_VIRUS_FOUND) {
            // Send an applicative exception (filebrowser, drive, ...)
            event.markBubbleException();
            throw new VirusScanException(DEFAULT_ERROR_VIRUS_FOUND_MESSAGE, ERROR_VIRUS_FOUND_LOCALIZED_MESSAGE, null);
        }
    }


}
