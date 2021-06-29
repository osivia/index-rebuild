package fr.index.cloud.ens.mutualization.copy.portlet.repository.command;

import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.IdRef;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Mutualization copy update Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see MutualizationCopyCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MutualizationCopyReplaceCommand extends MutualizationCopyCommand {

    /**
     * Target document identifier.
     */
    private final String targetId;


    /**
     * Constructor.
     *
     * @param source   source document
     * @param targetId target document identifier
     */
    public MutualizationCopyReplaceCommand(Document source, String targetId) {
        super(source);
        this.targetId = targetId;
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        // Target
        DocRef target = new IdRef(this.targetId);

        // Update properties
        documentService.update(target, this.getProperties());

        // Copy file BLOB
        this.copyBlob(documentService, target);

        // Update statistics
        this.updateStatistics(nuxeoSession);

        return target;
    }


    /**
     * Get properties.
     *
     * @return properties
     */
    private PropertyMap getProperties() {
        PropertyMap properties = new PropertyMap();

        // Source version
        properties.set("mtz:sourceVersion", this.getSource().getVersionLabel());
        
        // Source digest
        PropertyMap sourceMap = this.getSource().getProperties().getMap("file:content");
        if( sourceMap != null)  {
         properties.set("mtz:sourceDigest", sourceMap.getString("digest"));
        }

        return properties;
    }

}
