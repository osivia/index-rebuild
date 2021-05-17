package fr.index.cloud.ens.filebrowser.mutualized.portlet.repository;

import bsh.EvalError;
import fr.index.cloud.ens.filebrowser.mutualized.portlet.repository.command.GetMutualizedFileBrowserDocumentsCommand;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.PaginableDocuments;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortCriteria;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserWindowProperties;
import org.osivia.services.workspace.filebrowser.portlet.repository.FileBrowserRepositoryImpl;
import org.osivia.services.workspace.filebrowser.portlet.repository.command.GetFileBrowserDocumentsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.portlet.PortletException;
import java.util.List;

/**
 * Mutualized file browser portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserRepositoryImpl
 * @see MutualizedFileBrowserRepository
 */
@Repository
@Primary
public class MutualizedFileBrowserRepositoryImpl extends FileBrowserRepositoryImpl implements MutualizedFileBrowserRepository {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;


    /**
     * Constructor.
     */
    public MutualizedFileBrowserRepositoryImpl() {
        super();
    }


    @Override
    public List<Document> getDocuments(PortalControllerContext portalControllerContext, FileBrowserWindowProperties windowProperties, String parentPath) throws PortletException {
        return null;
    }


    @Override
    public PaginableDocuments getPaginableDocuments(PortalControllerContext portalControllerContext, FileBrowserWindowProperties windowProperties, String parentPath, int pageSize, int pageIndex, FileBrowserSortCriteria criteria) throws PortletException {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Parent document context
        NuxeoDocumentContext parentDocumentContext;
        if (parentPath == null) {
            parentDocumentContext = null;
        } else {
            parentDocumentContext = nuxeoController.getDocumentContext(parentPath);
        }

        // NXQL request
        String nxql;
        if (StringUtils.isEmpty(windowProperties.getNxql())) {
            nxql = null;
        } else if (BooleanUtils.isTrue(windowProperties.getBeanShell())) {
            try {
                nxql = this.beanShellInterpretation(nuxeoController, parentDocumentContext, windowProperties.getNxql());
            } catch (EvalError e) {
                throw new PortletException(e);
            }
        } else {
            nxql = windowProperties.getNxql();
        }

        // Nuxeo command
        INuxeoCommand command = this.applicationContext.getBean(GetMutualizedFileBrowserDocumentsCommand.class, nxql, pageSize, pageIndex, criteria);

        return (PaginableDocuments) nuxeoController.executeNuxeoCommand(command);
    }

}
