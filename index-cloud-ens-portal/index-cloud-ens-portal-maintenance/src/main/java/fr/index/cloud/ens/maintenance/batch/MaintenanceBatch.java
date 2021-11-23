package fr.index.cloud.ens.maintenance.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.portlet.PortletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.batch.AbstractBatch;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoServiceFactory;

/**
 * The Class ProcedureBatch.
 * 
 * Is executed periodically and purge procedures expired on in abnormal state
 */

public class MaintenanceBatch extends AbstractBatch {




    /** Portlet context. */
    private static PortletContext portletContext;

    /** The logger. */
    protected static Log log = LogFactory.getLog("PORTAL_MAINTENANCE");

    /** The scheduler definition property name */
    private static final String PROCEDURES_CRON = "index.maintenance.batch.cron";
    
    /** The export model procedure model name */
    private static final String PROCEDURE_EXPORT = "export_workspace";


    /** The export procedure processing step */
    private static final String EXPORT_PROCESSING_STEP = "1";
    
    /** The maximum delay for exports . */
    private static final int EXPORT_MAX_DELAY = 10;

    /** The enabled. */
    private final boolean enabled;

    /**
     * Instantiates a new procedure batch.
     */
    public MaintenanceBatch() {
        if (StringUtils.isNotEmpty(System.getProperty(PROCEDURES_CRON))) {
            enabled = true;
        } else
            enabled = false;
    }


    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isRunningOnMasterOnly() {
        return true;
    }

    @Override
    public String getJobScheduling() {
        // exemple "0/20 * * * * ?";
        return System.getProperty(PROCEDURES_CRON);
    }


  
    private NuxeoController getNuxeoController() {
        NuxeoController nuxeoController = new NuxeoController(portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void execute(Map<String, Object> parameters) {

        long begin = System.currentTimeMillis();

        log.info("Starting maintenance batch.");
        
        NuxeoController ctx = getNuxeoController();

        
        /* Pending exports */
        processExports(ctx);
        
        long end = System.currentTimeMillis();
        
        log.info("Ending maintenance batch. Elapsed time : " + (end-begin) + " ms.");
    }


    /**
     * Process exports.
     *
     * @param ctx the ctx
     */
    
    private void processExports(NuxeoController ctx) {
        Documents documents = (Documents)ctx.executeNuxeoCommand(new GetProcedureInstancesCommand(null, PROCEDURE_EXPORT, null, null));
        List<Document> exports = documents.list();
        
        log.info("Number of exports     : "+exports.size());
        
        for( Document document:documents.list())    {
            
            //log.debug(document.getPath()+ " MODEL=" +document.getProperties().get("pi:procedureModelWebId") + " STEP ="+document.getProperties().get("pi:currentStep"));    
            
            // The export is proceeded asynchronously, so it could abort ...
            // (for example, during a server stop)
            // We must delete the procedure because the user cannot retry if there is an export is running
            
            String step = (String) document.getProperties().get("pi:currentStep");
            
            if( StringUtils.equals(step, EXPORT_PROCESSING_STEP))  {
                Date lastModification = document.getDate("dc:modified");
                long elapsedMinutes = getDateDiff(lastModification,new Date(),TimeUnit.MINUTES);
                if( elapsedMinutes > EXPORT_MAX_DELAY)  {
                    ctx.executeNuxeoCommand(new RemoveDocumentCommand(document));
                    log.info("  Removing pending export : " + document.getPath()); 
                }
            }
        }
    }
    


    public void setPortletContext(PortletContext portletContext) {
        MaintenanceBatch.portletContext = portletContext;
    }


}
