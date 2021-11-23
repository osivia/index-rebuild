/*
 *
 */
package fr.index.cloud.ens.maintenance.batch;

import java.text.ParseException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.batch.IBatchService;
import org.osivia.portal.api.locator.Locator;

import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;


/**
 * The procedure batch declaration portlet
 * 
 */
public class MaintenanceBatchPortlet extends CMSPortlet {

   /** Batch coherence */
    private MaintenanceBatch batch = new MaintenanceBatch();

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

      
        // add batch in scheduler
        if(batch.isEnabled()) {
            IBatchService batchService = Locator.findMBean(IBatchService.class, IBatchService.MBEAN_NAME);
    
            try {
                batch.setPortletContext(getPortletContext());
                
                batchService.addBatch(batch);
            } catch (ParseException | PortalException e) {
                throw new PortletException(e);
            } 
        }
        
    }


    // @Override
    public void destroy() {

        super.destroy();

        
        if(batch.isEnabled()) {
            IBatchService batchService = Locator.findMBean(IBatchService.class, IBatchService.MBEAN_NAME);
            batchService.removeBatch(batch);
        }
        
    }
}
