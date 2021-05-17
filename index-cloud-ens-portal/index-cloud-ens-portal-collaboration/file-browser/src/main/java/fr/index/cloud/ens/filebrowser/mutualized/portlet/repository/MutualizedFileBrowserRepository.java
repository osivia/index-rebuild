package fr.index.cloud.ens.filebrowser.mutualized.portlet.repository;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PaginableDocuments;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortCriteria;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserWindowProperties;
import org.osivia.services.workspace.filebrowser.portlet.repository.FileBrowserRepository;

import javax.portlet.PortletException;
import java.util.List;

/**
 * Mutualized file browser portlet repository interface.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserRepository
 */
public interface MutualizedFileBrowserRepository extends FileBrowserRepository {

    /**
     * Get paginable documents.
     *
     * @param portalControllerContext portal controller context
     * @param windowProperties        window properties
     * @param parentPath              parent document path
     * @param pageSize                page size
     * @param pageIndex               page index
     * @param criteria                sort criteria
     * @return documents
     */
    PaginableDocuments getPaginableDocuments(PortalControllerContext portalControllerContext, FileBrowserWindowProperties windowProperties, String parentPath, int pageSize, int pageIndex, FileBrowserSortCriteria criteria) throws PortletException;

}
