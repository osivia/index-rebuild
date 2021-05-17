package fr.index.cloud.ens.filebrowser.mutualized.portlet.repository.command;

import fr.index.cloud.ens.filebrowser.mutualized.portlet.model.MutualizedFileBrowserSortField;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortCriteria;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sun.swing.StringUIClientPropertyKey;

/**
 * Get mutualized file browser documents Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetMutualizedFileBrowserDocumentsCommand implements INuxeoCommand {

    /**
     * NXQL request.
     */
    private final String nxql;
    /**
     * Page size.
     */
    private final int pageSize;
    /**
     * Page index.
     */
    private final int pageIndex;
    /**
     * Sort criteria.
     */
    private final FileBrowserSortCriteria criteria;


    /**
     * Constructor.
     *
     * @param nxql      NXQL request
     * @param pageSize  page size
     * @param pageIndex page index
     * @param criteria  sort criteria
     */
    public GetMutualizedFileBrowserDocumentsCommand(String nxql, int pageSize, int pageIndex, FileBrowserSortCriteria criteria) {
        super();
        this.nxql = nxql;
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.criteria = criteria;
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Nuxeo request
        StringBuilder nuxeoRequest = new StringBuilder();
        nuxeoRequest.append(this.nxql);

        // Sort
        MutualizedFileBrowserSortField field = (MutualizedFileBrowserSortField) criteria.getField();
        if (StringUtils.isNotEmpty(field.getField())) {
            nuxeoRequest.append(" ORDER BY ");
            nuxeoRequest.append(field.getField());
            nuxeoRequest.append(" ");
            if (criteria.isAlt()) {
                nuxeoRequest.append("DESC");
            } else {
                nuxeoRequest.append("ASC");
            }
        }

        // Query filter
        NuxeoQueryFilterContext queryFilterContext = new NuxeoQueryFilterContext(NuxeoQueryFilterContext.STATE_DEFAULT,
                InternalConstants.PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER);
        String filteredRequest = NuxeoQueryFilter.addPublicationFilter(queryFilterContext, nuxeoRequest.toString());

        // Operation request
        OperationRequest operationRequest = nuxeoSession.newRequest("Document.QueryES");
        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, "*");
        operationRequest.set("query", "SELECT * FROM Document WHERE " + filteredRequest);
        operationRequest.set("pageSize", this.pageSize);
        operationRequest.set("currentPageIndex", this.pageIndex);

        return operationRequest.execute();
    }


    @Override
    public String getId() {
        return null;
    }

}
