package fr.index.cloud.ens.search.common.portlet.repository.command;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import net.sf.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoadVocabularyCommand implements INuxeoCommand {

    /**
     * Operation identifier.
     */
    private static final String OPERATION_ID = "Document.GetVocabularies";


    /**
     * Vocabulary.
     */
    private final String vocabulary;


    /**
     * Constructor.
     *
     * @param vocabulary vocabulary
     */
    public LoadVocabularyCommand(String vocabulary) {
        super();
        this.vocabulary = vocabulary;
    }


    @Override
    public JSONArray execute(Session nuxeoSession) throws Exception {
        // Operation request
        OperationRequest request = nuxeoSession.newRequest(OPERATION_ID);
        request.setHeader(org.nuxeo.ecm.automation.client.Constants.HEADER_NX_SCHEMAS, "*");
        request.set("vocabularies", this.vocabulary);
        Object result = request.execute();

        JSONArray array;
        if (result instanceof Blob) {
            Blob blob = (Blob) result;
            String content = IOUtils.toString(blob.getStream(), CharEncoding.UTF_8);
            array = JSONArray.fromObject(content);
        } else {
            array = new JSONArray();
        }

        return array;
    }


    @Override
    public String getId() {
        return this.getClass().getName() + "/" + this.vocabulary;
    }

}
