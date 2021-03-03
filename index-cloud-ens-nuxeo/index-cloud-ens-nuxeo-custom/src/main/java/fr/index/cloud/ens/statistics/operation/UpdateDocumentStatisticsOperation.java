package fr.index.cloud.ens.statistics.operation;

import org.apache.commons.lang.BooleanUtils;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Update document statistics operation.
 *
 * @author Cédric Krommenhoek
 */
@Operation(id = UpdateDocumentStatisticsOperation.ID, category = Constants.CAT_DOCUMENT, label = "Update statistics")
public class UpdateDocumentStatisticsOperation {

    /**
     * Operation identifier.
     */
    public static final String ID = "Index.UpdateStatistics";


    /**
     * Core session.
     */
    @Context
    private CoreSession session;

    /**
     * Increments views indicator.
     */
    @Param(name = "incrementsViews", required = false, description = "Increments views indicator")
    private Boolean incrementsViews;

    /**
     * Increments downloads indicator.
     */
    @Param(name = "incrementsDownloads", required = false, description = "Increments downloads indicator")
    private Boolean incrementsDownloads;


    /**
     * Constructor.
     */
    public UpdateDocumentStatisticsOperation() {
        super();
    }


    /**
     * Run operation.
     *
     * @param document document
     */
    @OperationMethod
    public void run(DocumentModel document) {
        boolean incrementsViews = BooleanUtils.isTrue(this.incrementsViews);
        boolean incrementsDownloads = BooleanUtils.isTrue(this.incrementsDownloads);

        // Silent run
        UpdateDocumentStatisticsRunner runner = new UpdateDocumentStatisticsRunner(this.session, document, incrementsViews, incrementsDownloads);
        runner.silentRun(true);
    }

}
