package fr.index.cloud.ens.portal.discussion.portlet.repository;

import java.util.Date;
import java.util.List;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.index.cloud.ens.portal.discussion.portlet.model.DetailForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * The Class DeleteMessageCommand.
 * 
 * @author Jean-sébastien Steux
 */


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class DeleteMessageCommand implements INuxeoCommand  {

    
    DetailForm form;
    String messageId;
    
    
    /**
     * Constructor.
     * 
     * @param form creation form
     */
    public DeleteMessageCommand(DetailForm form, String messageId) {
        super();
        this.form = form;
        this.messageId = messageId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
 

        PropertyMap value = new PropertyMap();
        value.set("messageId", messageId);
        value.set("date", new Date(System.currentTimeMillis()));

       
        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.AddComplexProperty");
        request.setInput(new DocRef(form.getDocument().getId()));
        request.set("xpath", "disc:removedMessages");
        request.set("value", value);
        
        request.execute();

    
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return null;
    }

}
