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
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.runtime.api.Framework;


/**
 * Scan new files thanks to ICAP compatible scanner
 * 
 * @author jean-sébastien steux
 *
 */
public class ScanChecker {


    /** Log. */
    private static final Log log = LogFactory.getLog(ScanChecker.class);


    /** The max pool size. */
    int maxPoolSize = 50;

    /** The pool size. */
    int poolSize = 20;

    /** The keep alive time. */
    long keepAliveTime = 30;


    /** The instance. */
    static ScanChecker instance = null;

    protected ExecutorService threadPool = null;

    long waitingForServerRestart = 0;

    /**
     * build the pool
     * 
     * @return
     */
    protected ExecutorService getThreadPool() {

        if (threadPool == null) {
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(maxPoolSize);
            threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
        }

        return threadPool;
    }

    public static ScanChecker getInstance() {
        if (instance == null) {
            instance = new ScanChecker();
        }

        return instance;
    }


    /**
     * check the file and throws a VirusScanException if a virus is detected
     * 
     * @param docToScan
     */
    public ScanResult checkFile(DocumentModel docToScan, CoreSession session, boolean save, long timeout) {

        int errorCode = ScanResult.ERROR_CANNOT_CHECK;
        boolean toSave = false;

        // BlobHolder is defined for document having file
        BlobHolder bHolder = docToScan.getAdapter(BlobHolder.class);

        if (bHolder != null && bHolder.getBlob() != null) {

            try {
                if (log.isDebugEnabled())
                    log.debug("ScanListener.scan " + bHolder.getBlob().getFilename());

                String ICAPHost = Framework.getProperty("index.antivirus.icap.host");
                String ICAPPort = Framework.getProperty("index.antivirus.icap.port");
           

                if (StringUtils.isNotEmpty(ICAPHost) && StringUtils.isNotEmpty(ICAPPort)) {
                    
                    String ICAPService = Framework.getProperty("index.antivirus.icap.service");
                    if( ICAPService == null)
                        ICAPService = "";
                    
                    if (waitingForServerRestart == 0 || System.currentTimeMillis() > waitingForServerRestart + 60000 ) {
                        waitingForServerRestart = 0;

                        ICAP icap = new ICAP(ICAPHost, Integer.parseInt(ICAPPort), ICAPService, docToScan.getName(), bHolder.getBlob().getStream(), bHolder.getBlob().getLength());

                        Future<ICAPResult> future = getThreadPool().submit(icap);


                        
                        ICAPResult result = future.get(timeout, TimeUnit.SECONDS);

                        if (result != null) {
                            if (result.getStateProcessing() == ICAPResult.STATE_VIRUS_FOUND) {
                                String virusName = result.getVirusName();
                                if( virusName == null)
                                    virusName = "[unknown]";
                                log.warn("Virus '"+virusName+"' found in " + bHolder.getBlob().getFilename() + ".");
                                errorCode = ScanResult.ERROR_VIRUS_FOUND;
                            }

                            if (result.getStateProcessing() == ICAPResult.STATE_CHECKED) {
                                errorCode = ScanResult.ERROR_CLEAN;
                                toSave = removeFromQuarantine(docToScan, session);
                            }
                        }
                    }   else    {
                        // Server is not reachable
                        toSave = addToQuarantine(docToScan, session);
                        log.warn("ICAP Server is not yet reachable . File "+bHolder.getBlob().getFilename()+" is put in quarantine .");
                    }
                }


            } catch (Exception e) {

                // If the file can not have be scanned, it must be put in quarantine
                toSave = addToQuarantine(docToScan, session);

                boolean mustLogError = true;

                if (e instanceof TimeoutException) {
                    // Timeout -> no stack
                    log.warn("Timeout during scan of " +  bHolder.getFilePath() + " . File is put in quarantine .");
                    mustLogError = false;
                }

                if (e instanceof ExecutionException) {
                    ExecutionException exec = (ExecutionException) e;
                    if (exec.getCause() instanceof UnknownHostException || exec.getCause() instanceof IOException) {
                        // IO error -> no stack
                        log.error("Error during scan of " + bHolder.getFilePath() + " : " + exec.getCause().toString()
                                + ".  File is put in quarantine.");

                        waitingForServerRestart = System.currentTimeMillis();
                        mustLogError = false;
                    }
                }

                if (mustLogError)
                    // all other errors : stack for easier diagnostic
                    log.error("Error during scan of " +  bHolder.getFilePath() + " . File is put in quarantine .", e);
            }


        }

        return new ScanResult(errorCode, toSave);
    }


    /**
     * Add to quarantine
     * 
     * @param doc
     */
    private boolean addToQuarantine(DocumentModel doc, CoreSession session) {
        if (doc.getProperty("virusScan", "quarantineDate") == null) {
            doc.setProperty("virusScan", "quarantineDate", new java.util.Date());
            return true;
        }
        return false;
    }


    /**
     * Remove from quaratine (if needed)
     * 
     * @param doc
     */
    private boolean removeFromQuarantine(DocumentModel doc, CoreSession session) {
        if (doc.getProperty("virusScan", "quarantineDate") != null) {
            doc.setProperty("virusScan", "quarantineDate", null);
            return true;
        }
        return false;
    }

}
