package fr.index.cloud.ens.ws.nuxeo;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.DocRef;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Synchronize command
 * 
 * @author Jean-Sébastien
 */
public class SynchronizeCommand implements INuxeoCommand {

    private String path;

    public SynchronizeCommand(String path) {
        super();
        this.path = path;
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        OperationRequest request = nuxeoSession.newRequest("NuxeoDrive.SetSynchronization");

        request.getParameters().put("enable", "true");
        request.setInput(new DocRef(path));

        return request.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("/" + path);


        return builder.toString();
    }
}
