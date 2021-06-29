package fr.index.cloud.ens.mutualization.portlet.repository;

import fr.index.cloud.ens.mutualization.portlet.model.MutualizationForm;
import fr.index.cloud.ens.mutualization.portlet.repository.command.DisableMutualizationCommand;
import fr.index.cloud.ens.mutualization.portlet.repository.command.EnableMutualizationCommand;
import fr.index.cloud.ens.mutualization.portlet.repository.command.LoadVocabularyCommand;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Mutualization portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see MutualizationRepository
 */
@Repository
public class MutualizationRepositoryImpl implements MutualizationRepository {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;


    /**
     * Constructor.
     */
    public MutualizationRepositoryImpl() {
        super();
    }


    @Override
    public Document getDocument(PortalControllerContext portalControllerContext, String path) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Document context
        NuxeoDocumentContext documentContext = nuxeoController.getDocumentContext(path);

        return documentContext.getDocument();
    }


    @Override
    public List<String> getDocumentAncestors(PortalControllerContext portalControllerContext, String path) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Ancestors
        List<String> ancestors = new ArrayList<>();

        // Child path
        String childPath = StringUtils.substringBeforeLast(path, "/");

        // Loop indicator
        boolean loop = true;

        while (loop) {
            // Parent path
            String parentPath = StringUtils.substringBeforeLast(childPath, "/");
            // Parent document context
            NuxeoDocumentContext parentDocumentContext = nuxeoController.getDocumentContext(parentPath);
            // Parent document type
            DocumentType parentDocumentType = parentDocumentContext.getDocumentType();

            if ((parentDocumentType != null) && StringUtils.equals("Folder", parentDocumentType.getName())) {
                // Child document context
                NuxeoDocumentContext childDocumentContext = nuxeoController.getDocumentContext(childPath);
                // Child document
                Document childDocument = childDocumentContext.getDocument();

                // Add child title
                ancestors.add(childDocument.getTitle());

                // Loop on path
                childPath = parentPath;
            } else {
                // Stop loop
                loop = false;
            }
        }

        return ancestors;
    }


    @Override
    public JSONArray loadVocabulary(PortalControllerContext portalControllerContext, String vocabulary) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        nuxeoController.setCacheTimeOut(TimeUnit.HOURS.toMillis(1));
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT);

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(LoadVocabularyCommand.class, vocabulary);

        return (JSONArray) nuxeoController.executeNuxeoCommand(command);
    }


    @Override
    public void enableMutualization(PortalControllerContext portalControllerContext, MutualizationForm form, String documentPath, String mutualizedSpacePath) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(EnableMutualizationCommand.class, form, documentPath, mutualizedSpacePath);

        Document publishedDocument = (Document) nuxeoController.executeNuxeoCommand(command);
        
        // Update detail view in mutualized space
        nuxeoController.setDisplayLiveVersion("0");
        NuxeoDocumentContext mutualizedDocumentContext = nuxeoController.getDocumentContext(publishedDocument.getPath());
        mutualizedDocumentContext.reload();
    }


    @Override
    public void disableMutualization(PortalControllerContext portalControllerContext, String documentPath, String mutualizedSpacePath) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(DisableMutualizationCommand.class, documentPath, mutualizedSpacePath);

        nuxeoController.executeNuxeoCommand(command);
    }

}
