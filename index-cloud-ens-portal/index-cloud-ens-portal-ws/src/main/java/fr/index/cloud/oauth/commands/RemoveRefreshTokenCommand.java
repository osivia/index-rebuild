package fr.index.cloud.oauth.commands;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Removes a refresh command
 * 
 * @author Jean-Sébastien
 */
public class RemoveRefreshTokenCommand implements INuxeoCommand {

    
    private String docPath;
    private String token;
    private int index;
    
    public RemoveRefreshTokenCommand(String docPath, String token, int index) {
        super();
        this.docPath = docPath;
        this.token = token;
        this.index = index;
    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.RemoveProperty");
        request.setInput(new DocRef(docPath));
        request.set("xpath", "oatk:tokens/"+index);

        
        request.execute();

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("|");
        builder.append(this.token);
        return builder.toString();
    }
}
