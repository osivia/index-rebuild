package fr.index.cloud.ens.application.management;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;

import fr.index.cloud.ens.ext.etb.EtablissementService;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Applications search commane
 * 
 * @author Jean-Sébastien
 */
public class GetApplicationsByFilterCommand implements INuxeoCommand {

    /** The keyword. */
    private String keyword;
    
    /** The max result. */
    private int maxResults;
    
    public GetApplicationsByFilterCommand(String keyword, int maxResults) {
        super();
        this.keyword = keyword;
        this.maxResults = maxResults;
    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        

        // Clause
        StringBuilder clause = new StringBuilder();
        clause.append("( dc:title ILIKE  '%"+keyword+"%' OR ttc:webid STARTSWITH  '"+EtablissementService.APPLICATION_ID_PREFIX+keyword+"' )");
        
        clause.append("AND ecm:primaryType='OAuth2Application'");        

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.QueryES");
        request.set(Constants.HEADER_NX_SCHEMAS, "*");
        request.set("query", "SELECT * FROM Document WHERE " + clause.toString());
        
        
        request.set("pageSize", maxResults);
        request.set("currentPageIndex", 0);
        
  
        return request.execute();
        

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("|");
        builder.append(this.keyword);
        return builder.toString();
    }
}
