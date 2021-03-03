/**
 * 
 */
package fr.index.cloud.ens.mimetypes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;


/**
 * Adapt specific mimetype by file introspection(QCM, )...
 * 
 * @author jean-sébastien steux
 *
 */
public class MimeTypeListener implements EventListener {

    /** Log. */
    private static final Log log = LogFactory.getLog(MimeTypeListener.class);


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
                        adaptMimeType(docToUpdate);
                    }
                }
            }
        }


        if ((event.getContext() instanceof DocumentEventContext) && (DocumentEventTypes.ABOUT_TO_CREATE.equals(event.getName()))
                || (DocumentEventTypes.ABOUT_TO_IMPORT.equals(event.getName()))) {

            DocumentEventContext evtCtx = (DocumentEventContext) event.getContext();
            DocumentModel docToCreate = evtCtx.getSourceDocument();
            if (docToCreate.hasSchema(ToutaticeNuxeoStudioConst.CST_DOC_FILE_SCHEMA)) {
                adaptMimeType(docToCreate);
            }
        }

    }


    /**
     * adapt the mime type
     * 
     * 
     * @param event
     * @param docToScan
     */
    private void adaptMimeType(DocumentModel docToScan) {
        // BlobHolder is defined for document having file
        BlobHolder bHolder = docToScan.getAdapter(BlobHolder.class);
        try {
            if (bHolder != null && bHolder.getBlob() != null) {

                if (bHolder.getBlob().getFilename().endsWith(".xml")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(bHolder.getBlob().getStream());

                    Element root = document.getDocumentElement();
                    if ("quiz".equals(root.getNodeName()))
                        bHolder.getBlob().setMimeType("application/index-qcm+xml");
                }
            }
        } catch (Exception e) {
            // Don't raise exception
            log.error("can't parse " + bHolder.getBlob().getFilename(), e);
        }
    }


}
