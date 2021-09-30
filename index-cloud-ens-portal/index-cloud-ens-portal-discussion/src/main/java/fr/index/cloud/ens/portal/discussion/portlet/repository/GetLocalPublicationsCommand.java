package fr.index.cloud.ens.portal.discussion.portlet.repository;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;

/**
 * Get Discussion Nuxeo command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class GetLocalPublicationsCommand implements INuxeoCommand {



    String rootPath;
    
    /**
     * Constructor.
     * 

     */
    public GetLocalPublicationsCommand( String rootPath) {
       this.rootPath = rootPath;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Query
        StringBuilder query = new StringBuilder();

        query.append("ecm:path STARTSWITH '").append(this.rootPath).append("' ");

        query.append("AND ( ");
        // copied into personal space (readers)        
        query.append("(mtz:sourceWebId IS NOT NULL) ");
        // copied from personal space (author)
        query.append(" OR (mtz:enable IS NOT NULL) ");  
        query.append(" )");        
        // Query filter
        NuxeoQueryFilterContext queryFilterContext = new NuxeoQueryFilterContext(NuxeoQueryFilterContext.STATE_LIVE);
        String filteredRequest = NuxeoQueryFilter.addPublicationFilter(queryFilterContext, query.toString());

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.QueryES");
        request.set(Constants.HEADER_NX_SCHEMAS, "dublincore, toutatice, file, mutualization");
        request.set("query", filteredRequest);

        return request.execute();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getName());

        return builder.toString();
    }

}
