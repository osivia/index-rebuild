package fr.index.cloud.ens.statistics.operation;

import fr.toutatice.ecm.platform.core.helper.ToutaticeSilentProcessRunnerHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.Property;

import java.io.IOException;

/**
 * Update document statistics runner.
 *
 * @author Cédric Krommenhoek
 * @see ToutaticeSilentProcessRunnerHelper
 */
public class UpdateDocumentStatisticsRunner extends ToutaticeSilentProcessRunnerHelper {

    /**
     * WebId XPath.
     */
    private static final String WEBID_XPATH = "ttc:webid";
    /**
     * Views XPath.
     */
    private static final String VIEWS_XPATH = "mtz:views";
    /**
     * Downloads XPath.
     */
    private static final String DOWNLOADS_XPATH = "mtz:downloads";


    /**
     * Document.
     */
    private final DocumentModel document;
    /**
     * Increments views indicator.
     */
    private final boolean incrementsViews;
    /**
     * Increments downloads indicator.
     */
    private final boolean incrementsDownloads;

    /**
     * Log.
     */
    private final Log log;


    /**
     * Constructor.
     *
     * @param session             session
     * @param document            document
     * @param incrementsViews     increments views indicator
     * @param incrementsDownloads increments downloads indicator
     */
    public UpdateDocumentStatisticsRunner(CoreSession session, DocumentModel document, boolean incrementsViews, boolean incrementsDownloads) {
        super(session);
        this.document = document;
        this.incrementsViews = incrementsViews;
        this.incrementsDownloads = incrementsDownloads;

        // Log
        this.log = LogFactory.getLog(this.getClass());
    }


    @Override
    public void run() throws ClientException {
        // WebId
        String webId = this.document.getProperty(WEBID_XPATH).getValue(String.class);

        // Live document
        DocumentModel live;
        if (this.document.isProxy()) {
            String query = "SELECT * FROM Document WHERE ecm:isProxy = 0 AND ecm:isVersion = 0 AND ttc:webid = '" + webId + "'";
            DocumentModelList results = session.query(query);
            if (results.isEmpty()) {
                live = null;
            } else {
                live = results.get(0);
            }
        } else {
            live = this.document;
        }

        if (live == null) {
            this.log.error("Unable to find live document for webId: '" + webId + "'.");
        } else {
            if (this.incrementsViews) {
                this.increments(live, VIEWS_XPATH);
            }
            if (this.incrementsDownloads) {
                this.increments(live, DOWNLOADS_XPATH);
            }

            this.session.saveDocument(live);
        }
    }


    /**
     * Increments property value.
     *
     * @param live  live document
     * @param xpath property XPath
     */
    private void increments(DocumentModel live, String xpath) {
        Property property = live.getProperty(xpath);
        Integer value = property.getValue(Integer.class);

        try {
            DocumentHelper.setProperty(this.session, live, xpath, String.valueOf(value + 1));
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

}
