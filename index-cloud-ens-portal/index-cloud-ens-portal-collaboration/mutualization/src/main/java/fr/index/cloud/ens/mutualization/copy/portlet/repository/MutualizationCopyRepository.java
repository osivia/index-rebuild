package fr.index.cloud.ens.mutualization.copy.portlet.repository;

import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyForm;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import java.util.List;

/**
 * Mutualization copy portlet repository interface.
 *
 * @author Cédric Krommenhoek
 */
public interface MutualizationCopyRepository {

    /**
     * Get document.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return document
     */
    Document getDocument(PortalControllerContext portalControllerContext, String path) throws PortletException;


    /**
     * Get user workspace document.
     *
     * @param portalControllerContext portal controller context
     * @return document
     */
    Document getUserWorkspace(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get existing targets.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     * @return documents
     */
    List<Document> getExistingTargets(PortalControllerContext portalControllerContext, MutualizationCopyForm form) throws PortletException;


    /**
     * Replace.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     * @param targetId                target document identifier
     */
    void replace(PortalControllerContext portalControllerContext, MutualizationCopyForm form, String targetId) throws PortletException;


    /**
     * Copy.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     */
    void copy(PortalControllerContext portalControllerContext, MutualizationCopyForm form) throws PortletException;

}
