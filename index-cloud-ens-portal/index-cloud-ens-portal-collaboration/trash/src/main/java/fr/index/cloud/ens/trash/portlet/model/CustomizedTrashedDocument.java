package fr.index.cloud.ens.trash.portlet.model;

import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import org.osivia.services.workspace.portlet.model.TrashedDocument;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Customized trashed document java-bean.
 *
 * @author Cédric Krommenhoek
 * @see TrashedDocument
 */
@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomizedTrashedDocument extends TrashedDocument {

    /** Folderish document indicator. */
    private boolean folderish;


    /**
     * Constructor.
     *
     * @param document document DTO
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public CustomizedTrashedDocument(DocumentDTO document) {
        super(document);
    }


    public boolean isFolderish() {
        return folderish;
    }

    public void setFolderish(boolean folderish) {
        this.folderish = folderish;
    }
}
