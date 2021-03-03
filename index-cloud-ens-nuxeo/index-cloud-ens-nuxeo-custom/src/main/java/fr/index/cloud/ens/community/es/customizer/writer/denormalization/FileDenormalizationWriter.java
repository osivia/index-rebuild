/**
 * 
 */
package fr.index.cloud.ens.community.es.customizer.writer.denormalization;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;

import fr.toutatice.ecm.es.customizer.writers.api.AbstractCustomJsonESWriter;


/**
 * copies live view ant download to proxy
 * 
 * @author jean-sébastien
 *
 */
public class FileDenormalizationWriter extends AbstractCustomJsonESWriter {

    /** Log. */
    private static final Log log = LogFactory.getLog(FileDenormalizationWriter.class);

    private static final String SCHEMA = "mutualization";
    private static final String VIEWS_XPATH = "mtz:views";
    private static final String DOWNLOAD_XPATH = "mtz:downloads";

    private static final String VIEWS_DENORMALIZED_FIELD = "mtz:liveviews";
    private static final String DOWNLOAD_DENORMALIZED_FIELD = "mtz:livedownloads";

    @Override
    public boolean accept(DocumentModel doc) {

        boolean accept = doc.hasSchema(SCHEMA) && doc.isProxy();
        return accept;
    }

    @Override
    public void writeData(JsonGenerator jsonGenerator, DocumentModel document, String[] schemas, Map<String, String> contextParameters) throws IOException {

        DocumentModel sourceDocument = session.getSourceDocument(document.getRef());

        if (sourceDocument != null) {

            DocumentModel liveDocument = session.getDocument(new PathRef(sourceDocument.getPath().toString()));

            if (liveDocument != null) {

                Long views = (Long) liveDocument.getPropertyValue(VIEWS_XPATH);
                jsonGenerator.writeNumberField(VIEWS_DENORMALIZED_FIELD, views);

                Long downloads = (Long) liveDocument.getPropertyValue(DOWNLOAD_XPATH);
                jsonGenerator.writeNumberField(DOWNLOAD_DENORMALIZED_FIELD, downloads);

                Map<String, String> bmap = liveDocument.getBinaryFulltext();
                if (bmap != null && !bmap.isEmpty()) {
                    for (Map.Entry<String, String> item : bmap.entrySet()) {
                        String value = item.getValue();
                        if (value != null) {
                            jsonGenerator.writeStringField("ecm:" + item.getKey(), value);
                        }
                    }
                }
            }
        }

    }

}
