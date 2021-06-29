package fr.index.cloud.ens.mutualization.portlet.repository;

import fr.index.cloud.ens.mutualization.portlet.model.MutualizationForm;
import net.sf.json.JSONArray;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import java.util.List;

/**
 * Mutualization portlet repository interface.
 *
 * @author Cédric Krommenhoek
 */
public interface MutualizationRepository {

    /**
     * Enable property.
     */
    String ENABLE_PROPERTY = "mtz:enable";
    /**
     * Title property.
     */
    String TITLE_PROPERTY = "mtz:title";
    /**
     * Keywords property.
     */
    String KEYWORDS_PROPERTY = "idxcl:keywords";
    /**
     * Document types property.
     */
    String DOCUMENT_TYPES_PROPERTY = "idxcl:documentTypes";
    /**
     * Levels property.
     */
    String LEVELS_PROPERTY = "idxcl:levels";
    /**
     * Subjects property.
     */
    String SUBJECTS_PROPERTY = "idxcl:subjects";
    /**
     * Issued property.
     */
    String ISSUED_PROPERTY = "dc:issued";

    /**
     * Get document.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return document
     */
    Document getDocument(PortalControllerContext portalControllerContext, String path) throws PortletException;


    /**
     * Get document ancestors.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return ancestors
     */
    List<String> getDocumentAncestors(PortalControllerContext portalControllerContext, String path) throws PortletException;


    /**
     * Load vocabulary.
     *
     * @param portalControllerContext portal controller context
     * @param vocabulary              vocabulary name
     * @return JSON array
     */
    JSONArray loadVocabulary(PortalControllerContext portalControllerContext, String vocabulary) throws PortletException;


    /**
     * Enable mutualization.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     * @param documentPath            document path
     * @param mutualizedSpacePath     mutualized space path
     */
    void enableMutualization(PortalControllerContext portalControllerContext, MutualizationForm form, String documentPath, String mutualizedSpacePath) throws PortletException;


    /**
     * Disable mutualization.
     *
     * @param portalControllerContext portal controller context
     * @param documentPath            document path
     * @param mutualizedSpacePath     mutualized space path
     */
    void disableMutualization(PortalControllerContext portalControllerContext, String documentPath, String mutualizedSpacePath) throws PortletException;

}
