package fr.index.cloud.ens.maintenance.batch;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;



import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;

public class GetProcedureInstancesCommand implements INuxeoCommand {

    /** Number of procs returned */
    public static final String PAGE_SIZE = "200";
    
    private final String modelId;
    /** Invitation state. */
    private final String step;
    /** Invitation uid. */
    private String uid;    
    /** Date */
    private Date referenceDate;
    

    /**
     * Constructor.
     * 
     * @param invitationState invitation state
     */
    public GetProcedureInstancesCommand(String step, String modelId, String uid, Date referenceDate) {

        this.step = step;
        this.modelId = modelId;
        this.uid = uid;
        this.referenceDate = referenceDate;
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Clause
        StringBuilder clause = new StringBuilder();
        clause.append("ecm:primaryType = 'ProcedureInstance' ");

        if (modelId != null) {
            clause.append(" AND pi:procedureModelWebId = 'procedure_"+modelId+"' ");
        }
        if (this.step != null) {
            clause.append(" AND pi:currentStep = '")
                    .append(this.step).append("' ");
        }
        /*
        if (this.uid != null) {
            clause.append(" AND pi:globalVariablesValues.").append(MemberManagementRepository.PERSON_UID_PROPERTY).append(" IN (");
            clause.append("'").append(uid).append("')");
        }
        */
        if (this.referenceDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            clause.append("AND dc:modified <= DATE '"+sdf.format(referenceDate)+"' ");

        }


        clause.append(" ORDER BY dc:created");
        
        
        // Filtered clause
        String filteredClause = NuxeoQueryFilter.addPublicationFilter(NuxeoQueryFilterContext.CONTEXT_LIVE, clause.toString());

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.QueryES");
        request.set(Constants.HEADER_NX_SCHEMAS, "dublincore, procedureInstance");
        request.set("query", "SELECT * FROM Document WHERE " + filteredClause);
        request.set("pageSize", PAGE_SIZE);
        request.set("currentPageIndex", 0);

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
//        if (this.workspaceId != null) {
//            builder.append(this.workspaceId);
//        }
//        builder.append("/");
//        if (this.modelId != null) {
//            builder.append(this.modelId);
//        }
//        builder.append("/");
        if (this.step != null) {
            builder.append(this.step);
        }
        builder.append("/");
//        if (this.identifiers != null) {
//            builder.append(StringUtils.join(this.identifiers, ","));
//        }
        return builder.toString();
    }
}