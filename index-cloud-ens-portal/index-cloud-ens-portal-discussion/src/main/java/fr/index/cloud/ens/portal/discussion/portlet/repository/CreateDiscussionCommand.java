package fr.index.cloud.ens.portal.discussion.portlet.repository;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class CreateDiscussionCommand implements INuxeoCommand  {


    
    DiscussionCreation discussion;
    
    
    /**
     * Constructor.
     * 
     * @param form creation form
     */
    public CreateDiscussionCommand(DiscussionCreation discussion) {
        super();
        this.discussion = discussion;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        // Parent
        DocRef parent = new DocRef("/default-domain/procedures/procedures-instances");

        // Properties
        PropertyMap properties = new PropertyMap();
        properties.set("dc:title", discussion.getMessage());
        properties.set("disc:type", discussion.getType());
        properties.set("disc:target", discussion.getTarget());
        properties.set("disc:participants", StringUtils.trimToNull(StringUtils.join(discussion.getParticipants(), ",")));
       

        Document document = documentService.createDocument(parent, "Discussion", null, properties, true);
        
        
        // fetch webId
        OperationRequest request = nuxeoSession.newRequest("Document.FetchLiveDocument");
        request.setHeader(Constants.HEADER_NX_SCHEMAS, "*");
        request.set("value", document.getPath());
        document = (Document) request.execute();
        
        return document;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return null;
    }

}
