package fr.index.cloud.ens.virusscan;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * Virus scanning
 * 
 * @author jssteux
 */
@Operation(id = VirusScanOperation.ID, category = Constants.CAT_DOCUMENT, label = "Scan virus",
        description = "Checks  if the file blob contains a virus using the ICAP protocole. Returns false if the file contains a virus, true otherwise")

public class VirusScanOperation {

    /** The operation ID. */
    public static final String ID = "Index.ScanVirus";
    
    /** The timeout for scan */
    public static final long OPERATION_TIMEOUT = 180;

    private static final Log log = LogFactory.getLog(VirusScanOperation.class);

    /**
     * Error code
     */
 

    @Context
    protected CoreSession session;


    @Param(name = "path", required = true)
    protected String path;

    @OperationMethod
    public Object run() throws Exception {

        JSONArray rowInfosPubli = new JSONArray();
        JSONObject infos = new JSONObject();

        int errorCode;

        DocumentModel doc = null;
        try {
            
            DocumentRef docRef = new PathRef(path);
            doc = session.getDocument(docRef);
           
            ScanResult res = ScanChecker.getInstance().checkFile(doc, session, true, OPERATION_TIMEOUT);
            
            errorCode = res.getErrorCode();
            
            if (res.isModified())
                ToutaticeDocumentHelper.saveDocumentSilently(session, doc, true);
        }

        catch (ClientException ce) {
            errorCode = ScanResult.ERROR_CANNOT_CHECK;
            log.warn("Failed to scan document with path  '" + path + "', error:" + ce.getMessage());
        }

        // Send result
        infos.element("error", errorCode);
        rowInfosPubli.add(infos);
        return createBlob(rowInfosPubli);
    }


   
    /**
     * Create a blob for response
     * 
     * @param json
     * @return
     */
    private Blob createBlob(JSONArray json) {
        return new StringBlob(json.toString(), "application/json");
    }


}
