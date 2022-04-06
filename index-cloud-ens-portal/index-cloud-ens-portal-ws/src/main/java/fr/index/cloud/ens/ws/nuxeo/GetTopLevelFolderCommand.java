package fr.index.cloud.ens.ws.nuxeo;

import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;


/**
 * Stores a refresh token
 * 
 * @author Jean-Sébastien
 */
public class GetTopLevelFolderCommand implements INuxeoCommand {

    public GetTopLevelFolderCommand() {
        super();

    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        // Operation request
        OperationRequest request = nuxeoSession.newRequest("NuxeoDrive.GetTopLevelFolder");


        return request.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());

        return builder.toString();
    }
}
