package fr.index.cloud.ens.test.portlets.content.controller;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.DocRef;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

public class GetRootCommand implements INuxeoCommand {

    private final String userName;
    
    public GetRootCommand(String userName) {
        super();
        this.userName = userName;
    }

    @Override
    public Object execute(Session session) throws Exception {
        OperationRequest mySpace = session.newRequest("Services.GetToutaticeUserProfile");
        mySpace.set("username", userName);
        org.nuxeo.ecm.automation.client.model.Document refDoc = (org.nuxeo.ecm.automation.client.model.Document) mySpace.execute();

        org.nuxeo.ecm.automation.client.model.Document space = (org.nuxeo.ecm.automation.client.model.Document) session
                .newRequest("Document.FetchLiveDocument").setHeader(org.nuxeo.ecm.automation.client.Constants.HEADER_NX_SCHEMAS, "*").set("value", refDoc)
                .execute();

        String rootPath = space.getPath().substring(0, space.getPath().lastIndexOf('/')) + "/documents";

        org.nuxeo.ecm.automation.client.model.Document documentRoot = (org.nuxeo.ecm.automation.client.model.Document) session
                .newRequest("Document.FetchLiveDocument").setHeader(org.nuxeo.ecm.automation.client.Constants.HEADER_NX_SCHEMAS, "*")
                .set("value", new DocRef(rootPath)).execute();

        return documentRoot;
    }

    @Override
    public String getId() {
        return this.getClass().getCanonicalName()+"/"+userName;
    }

}
