/**
 * 
 */
package fr.index.cloud.ens.community.es.customizer.listener.denormalization;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;


import fr.toutatice.ecm.es.customizer.listeners.denormalization.AbstractDenormalizationESListener;

/**
 * 
 * Reindex proxy if live is modified
 * (to add numbers of views and download)
 * 
 * @author jean-sébastien
 *
 */
public class FileDenormalizationListener extends AbstractDenormalizationESListener {
    
    /** Schema. */
    private static final String SCHEMA = "mutualization";  
    private static final String COMMUNITY_PATH = "/default-domain/communaute";   

    /** Log. */
    private static final Log log = LogFactory.getLog(FileDenormalizationListener.class);
    
	@Override
	protected boolean needToReIndex(DocumentModel sourceDocument, String eventId) {
		boolean needs = false;
		

		
		if(DocumentEventTypes.BEFORE_DOC_UPDATE.equals(eventId)) {
		    needs = sourceDocument.hasSchema(SCHEMA) && ! sourceDocument.isProxy();
	    }
		
		return needs;
	}

	@Override
	protected void stackCommands(CoreSession session, DocumentModel sourceDocument, String eventId) {
		// Get proxies if any
	    
	    DocumentRef folderRef = new PathRef(COMMUNITY_PATH);
        DocumentModelList proxies = session.getProxies(sourceDocument.getRef(), folderRef);
        
        for (DocumentModel source : proxies) {
            super.getEsInlineListener().stackCommand(source, eventId, true);
        }

	}

}
