package fr.index.cloud.ens.ws.commands;



import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;

/**
 * Fetch by publication id command.
 *
 * @author Jean-Sébastien Steux
 * @see INuxeoCommand
 */
public class FetchByPubIdCommand implements INuxeoCommand {

    /** publication ID  */
    private final String pubId;



    /**
     * Constructor.
     *
     * @param parentId parent Nuxeo document identifier
     * @param state Nuxeo query filter context state
     */
    public FetchByPubIdCommand(String pubId) {
        super();
        this.pubId = pubId;
   }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Clause
        StringBuilder clause = new StringBuilder();
        clause.append("rshr:targets/*/pubId = '"+pubId+"'");


        // Nuxeo query filter context
        NuxeoQueryFilterContext filterContext = new NuxeoQueryFilterContext(NuxeoQueryFilterContext.STATE_LIVE_N_PUBLISHED);

        // Filtered clause
        String filteredClause = NuxeoQueryFilter.addPublicationFilter(filterContext, clause.toString());

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.QueryES");
        request.set(Constants.HEADER_NX_SCHEMAS, "*");
        request.set("query", "SELECT * FROM Document WHERE " + filteredClause);

        return request.execute();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getName());
        builder.append("/");
        builder.append(this.pubId);
        return builder.toString();
    }

}
