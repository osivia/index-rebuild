/**
 * 
 */
package fr.index.cloud.ens.metadata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;


/**
 * Avoid duplication of share / publication properties
 * 
 * @author jeanseb
 *
 */
public class DuplicationListener implements EventListener {

    /** Log. */
    private static final Log log = LogFactory.getLog(DuplicationListener.class);

  

    
    private List<String> nonDuplicateSchemas = Arrays.asList("resourceSharing","mutualization");

    @Override
    public void handleEvent(Event event) throws ClientException {

        if (log.isDebugEnabled())
            log.debug("DuplicationListener.handleEvent " + event.getName());

        if ((DocumentEventTypes.DOCUMENT_CREATED_BY_COPY.equals(event.getName()))) {
            DocumentEventContext evtCtx = (DocumentEventContext) event.getContext();


            DocumentModel copiedDoc = evtCtx.getSourceDocument();

            for(String schema: nonDuplicateSchemas) {
                if (copiedDoc.hasSchema(schema)) {
                    Map<String, Object> properties = copiedDoc.getProperties(schema);
                if (properties != null) {
                    // Reset properties
                    Map<String, Object> resetMap = new HashMap<>();
    
                    // Hard reset to null
                    for (String propName : properties.keySet()) {
                        resetMap.put(propName, null);
    
                        if (log.isTraceEnabled()) {
                            log.trace(String.format("Property [%s] reset to null", propName));
                        }
                    }
    
                    // Update document model
                        copiedDoc.setProperties(schema, resetMap);
                    }
                }
            }
        }
    }

}
