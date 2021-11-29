package fr.index.cloud.oauth.commands;

import java.util.Date;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Stores a refresh token
 * 
 * @author Jean-Sébastien
 */
public class StoreRefreshTokenCommand implements INuxeoCommand {

    
    private String docPath;
    private String token;
    private String authentication;
    private Date expirationDate;
    
    
    public StoreRefreshTokenCommand(String docPath, String token, Date expirationDate, String authentication) {
        super();
        this.docPath = docPath;
        this.token = token;
        this.authentication = authentication;
        this.expirationDate = expirationDate;
    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        PropertyMap value = new PropertyMap();
        value.set("value", token);
        value.set("authentication", authentication);
        value.set("expiration", expirationDate);

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.AddComplexProperty");
        request.setInput(new DocRef(docPath));
        request.set("xpath", "oatk:tokens");
        request.set("value", value);
        
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
