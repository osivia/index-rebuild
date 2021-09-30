package fr.index.cloud.ens.portal.discussion.portlet.repository;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class AddMessageCommand implements INuxeoCommand  {

    
    DetailForm form;
    
    
    /**
     * Constructor.
     * 
     * @param form creation form
     */
    public AddMessageCommand(DetailForm form) {
        super();
        this.form = form;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        PropertyMap value = new PropertyMap();
        String content = form.getNewMessage();
         

        String[] lines = StringUtils.split(content, "\n");
        for (int i = 0; i < lines.length; i++) {
            String line = StringUtils.trim(lines[i]);

            if (i < lines.length - 1) {
                line += "\\";
            }

            lines[i] = line;
        }

        content = StringUtils.join(lines, "\n");

       
        value.set("content", content);
        value.set("author", form.getAuthor());
        value.set("date", new Date(System.currentTimeMillis()));

       
        // Operation request
        OperationRequest request = nuxeoSession.newRequest("Document.AddComplexProperty");
        request.setInput(new DocRef(form.getDocument().getId()));
        request.set("xpath", "disc:messages");
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
