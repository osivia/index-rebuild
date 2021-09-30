package fr.index.cloud.ens.portal.discussion.portlet.repository;

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
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;

/**
 * Get Discussions
 * 
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetDiscussionsByPublicationCommand implements INuxeoCommand {


    /** The publication id. */
    private String publicationId;


    /**
     * Instantiates a new gets the discussions by publication command.
     *
     * @param publicationId the publication id
     */
    public GetDiscussionsByPublicationCommand(String publicationId) {
        super();
        this.publicationId = publicationId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        StringBuilder query = new StringBuilder();

        query.append("( disc:type = '"+DiscussionDocument.TYPE_USER_COPY+"' AND disc:target = '"+publicationId+"' )");


        // Query filter
        NuxeoQueryFilterContext queryFilterContext = new NuxeoQueryFilterContext(NuxeoQueryFilterContext.STATE_LIVE);
        String filteredRequest = NuxeoQueryFilter.addPublicationFilter(queryFilterContext, query.toString());
        

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.QueryES");
        request.set(Constants.HEADER_NX_SCHEMAS, "dublincore, toutatice, discussion");
        request.set("query", filteredRequest.toString());

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
