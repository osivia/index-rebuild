package fr.index.cloud.ens.trash.portlet.repository;

import fr.index.cloud.ens.trash.portlet.model.CustomizedTrashedDocument;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.services.workspace.portlet.model.TrashedDocument;
import org.osivia.services.workspace.portlet.repository.TrashRepository;
import org.osivia.services.workspace.portlet.repository.TrashRepositoryImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Customized trash portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see TrashRepositoryImpl
 * @see TrashRepository
 */
@Repository
@Primary
public class CustomizedTrashRepositoryImpl extends TrashRepositoryImpl implements TrashRepository {

    /**
     * Constructor.
     */
    public CustomizedTrashRepositoryImpl() {
        super();
    }


    @Override
    protected TrashedDocument getTrashedDocument(NuxeoController nuxeoController, Document document) throws CMSException {
        CustomizedTrashedDocument trashedDocument = (CustomizedTrashedDocument) super.getTrashedDocument(nuxeoController, document);
        
        if(trashedDocument == null)
            return null;

        // Document type
        DocumentType type;
        if (trashedDocument.getDocument() == null) {
            type = null;
        } else {
            type = trashedDocument.getDocument().getType();
        }

        // Folderish indicator
        boolean folderish = ((type != null) && type.isFolderish());
        trashedDocument.setFolderish(folderish);

        return trashedDocument;
    }

}
