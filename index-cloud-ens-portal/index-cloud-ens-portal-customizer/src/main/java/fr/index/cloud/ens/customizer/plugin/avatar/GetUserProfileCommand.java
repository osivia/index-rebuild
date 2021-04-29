package fr.index.cloud.ens.customizer.plugin.avatar;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Document;

/**
 * Get user profile Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see INuxeoCommand
 */
public class GetUserProfileCommand implements INuxeoCommand {

    /**
     * Get user profile operation identifier.
     */
    private static final String GET_USER_PROFILE_OPERATION_ID = "Services.GetToutaticeUserProfile";
    /**
     * Fetch document operation identifier.
     */
    private static final String FETCH_DOCUMENT_OPERATION_ID = "Document.FetchLiveDocument";


    /**
     * User identifier.
     */
    private final String uid;


    /**
     * Constructor.
     *
     * @param uid user identifier
     */
    public GetUserProfileCommand(String uid) {
        this.uid = uid;
    }


    @Override
    public Document execute(Session nuxeoSession) throws Exception {
        // Get user profile
        OperationRequest operation1 = nuxeoSession.newRequest(GET_USER_PROFILE_OPERATION_ID);
        operation1.set("username", this.uid);
        Document userProfile = (Document) operation1.execute();

        // Get fetched user profile
        Document fetchedUserProfile;
        if (userProfile == null) {
            fetchedUserProfile = null;
        } else {
            OperationRequest operation2 = nuxeoSession.newRequest(FETCH_DOCUMENT_OPERATION_ID);
            operation2.setHeader(Constants.HEADER_NX_SCHEMAS, "*");
            operation2.set("value", userProfile.getPath());
            operation2.set("permission", "Read");

            fetchedUserProfile = (Document) operation2.execute();
        }

        return fetchedUserProfile;
    }


    @Override
    public String getId() {
        return this.getClass().getSimpleName() + "/" + this.uid;
    }

}
