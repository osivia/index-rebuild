package fr.index.cloud.ens.portal.discussion.portlet.repository;

import java.util.Set;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;
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

public class GetPublication implements INuxeoCommand {

    private static final String MUTUALIZED_SPACE_PATH = System.getProperty("config.mutualized.path");



    /** The web id. */
    String webId;
    
    /**
     * Constructor.
     * 

     */
    public GetPublication( String webId) {
       this.webId = webId;
  }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
  
        // Nuxeo request
        StringBuilder nuxeoRequest = new StringBuilder();
        nuxeoRequest.append("ecm:path STARTSWITH '").append(MUTUALIZED_SPACE_PATH).append("' ");
        
   
        
        nuxeoRequest.append("AND ttc:webid = '"+webId+"'");

        // Query filter
        String filteredRequest = NuxeoQueryFilter.addPublicationFilter(NuxeoQueryFilterContext.CONTEXT_DEFAULT, nuxeoRequest.toString());

        // Operation request
        OperationRequest operationRequest = nuxeoSession.newRequest("Document.QueryES");
        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, "dublincore,toutatice,file");
        operationRequest.set("query", "SELECT * FROM Document WHERE " + filteredRequest);

        // Results
        return operationRequest.execute();

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
