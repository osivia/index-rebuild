package fr.index.cloud.ens.mutualization.copy.portlet.repository;

import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyForm;
import fr.index.cloud.ens.mutualization.copy.portlet.repository.command.GetDocumentsBySourceWebIdCommand;
import fr.index.cloud.ens.mutualization.copy.portlet.repository.command.MutualizationCopyDuplicateCommand;
import fr.index.cloud.ens.mutualization.copy.portlet.repository.command.MutualizationCopyReplaceCommand;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import javax.portlet.PortletException;
import java.util.List;

/**
 * Mutualization copy portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see MutualizationCopyRepository
 */
@Repository
public class MutualizationCopyRepositoryImpl implements MutualizationCopyRepository {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * CMS service locator.
     */
    @Autowired
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public MutualizationCopyRepositoryImpl() {
        super();
    }


    @Override
    public Document getDocument(PortalControllerContext portalControllerContext, String path) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        return this.getDocument(nuxeoController, path);
    }


    @Override
    public Document getUserWorkspace(PortalControllerContext portalControllerContext) throws PortletException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // CMS item
        CMSItem cmsItem;
        try {
            cmsItem = cmsService.getUserWorkspace(cmsContext);
        } catch (CMSException e) {
            throw new PortletException(e);
        }

        // Document
        return (Document) cmsItem.getNativeItem();
    }


    @Override
    public List<Document> getExistingTargets(PortalControllerContext portalControllerContext, MutualizationCopyForm form) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Source document
        Document source = this.getDocument(nuxeoController, form.getDocumentPath());
        // Source document webId
        String sourceWebId = source.getString("ttc:webid");

        // Target document
        Document target = this.getDocument(nuxeoController, form.getTargetPath());
        // Target document identifier
        String targetId = target.getId();

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(GetDocumentsBySourceWebIdCommand.class, sourceWebId, targetId);
        Documents documents = (Documents) nuxeoController.executeNuxeoCommand(command);

        return documents.list();
    }


    @Override
    public void replace(PortalControllerContext portalControllerContext, MutualizationCopyForm form, String targetId) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Source document
        // must be refreshed to  synchronized digest with documentService.getBlob
        Document source = this.getRefreshedDocument(nuxeoController, form.getDocumentPath());

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(MutualizationCopyReplaceCommand.class, source, targetId);
        nuxeoController.executeNuxeoCommand(command);
        
        // Reload local cache (update local view)
        Document target = nuxeoController.fetchDocument(targetId);
        nuxeoController.getDocumentContext(target.getPath()).reload();
    }


    @Override
    public void copy(PortalControllerContext portalControllerContext, MutualizationCopyForm form) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Source document
        // must be refreshed to  synchronized digest with documentService.getBlob
        
        Document source = this.getRefreshedDocument(nuxeoController, form.getDocumentPath());

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(MutualizationCopyDuplicateCommand.class, source, form.getTargetPath());
        nuxeoController.executeNuxeoCommand(command);
    }


    /**
     * Get document.
     *
     * @param nuxeoController Nuxeo controller
     * @param path            document path
     * @return document
     */
    private Document getDocument(NuxeoController nuxeoController, String path) {
        // Document context
        NuxeoDocumentContext documentContext = nuxeoController.getDocumentContext(path);

        return documentContext.getDocument();
    }
    
    
    private Document getRefreshedDocument(NuxeoController nuxeoController, String path) {
        // Document context
        NuxeoDocumentContext documentContext = nuxeoController.getDocumentContext(path);
        
        documentContext.reload();

        return documentContext.getDocument();
    }
    

}
