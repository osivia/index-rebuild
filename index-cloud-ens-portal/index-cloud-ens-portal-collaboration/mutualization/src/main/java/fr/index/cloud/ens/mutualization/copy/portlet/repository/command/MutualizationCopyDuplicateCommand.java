package fr.index.cloud.ens.mutualization.copy.portlet.repository.command;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.model.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutualization copy duplicate Nuxeo command.
 *
 * @author Cédric Krommenhoek
 * @see MutualizationCopyCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MutualizationCopyDuplicateCommand extends MutualizationCopyCommand {

    /**
     * Target parent path.
     */
    private final String targetParentPath;


    /**
     * Constructor.
     *
     * @param source           source document
     * @param targetParentPath target parent path
     */
    public MutualizationCopyDuplicateCommand(Document source, String targetParentPath) {
        super(source);
        this.targetParentPath = targetParentPath;
    }


    @Override
    public Object execute(Session nuxeoSession) throws Exception {
        // Document service
        DocumentService documentService = nuxeoSession.getAdapter(DocumentService.class);

        // Parent document reference
        DocRef parent = new PathRef(this.targetParentPath);

        // Document creation
        DocRef target = documentService.createDocument(parent, this.getSource().getType(), null, this.getProperties());

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

        // Title
        properties.set("dc:title", this.getSource().getTitle());
        // Source webId
        properties.set("mtz:sourceWebId", this.getSource().getString("ttc:webid"));
        // Source version
        properties.set("mtz:sourceVersion", this.getSource().getVersionLabel());
        
        // Source digest
        PropertyMap sourceMap = this.getSource().getProperties().getMap("file:content");
        if( sourceMap != null)  {
         properties.set("mtz:sourceDigest", sourceMap.getString("digest"));
        }
        
        // Keywords
        properties.set("idxcl:keywords", this.getListProperty("idxcl:keywords"));
        // Document types
        properties.set("idxcl:documentTypes", this.getListProperty("idxcl:documentTypes"));
        // Levels
        properties.set("idxcl:levels", this.getListProperty("idxcl:levels"));
        // Subjects
        properties.set("idxcl:subjects", this.getListProperty("idxcl:subjects"));

        return properties;
    }


    /**
     * Get list property.
     *
     * @param name property name
     * @return property value
     */
    private String getListProperty(String name) {
        PropertyList list = this.getSource().getProperties().getList(name);
        List<String> values;
        if ((list == null) || list.isEmpty()) {
            values = null;
        } else {
            values = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                values.add(list.getString(i));
            }
        }

        return StringUtils.join(values, ",");
    }

}
