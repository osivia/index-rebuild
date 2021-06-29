package fr.index.cloud.ens.mutualization.portlet.service;

import fr.index.cloud.ens.mutualization.portlet.model.MutualizationForm;
import net.sf.json.JSONArray;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import java.io.IOException;

/**
 * Mutualization portlet service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface MutualizationService {

    /**
     * Portlet instance.
     */
    String PORTLET_INSTANCE = "index-cloud-ens-mutualization-instance";

    /**
     * Document path window property.
     */
    String DOCUMENT_PATH_WINDOW_PROPERTY = "osivia.mutualize.path";


    /**
     * Get form.
     *
     * @param portalControllerContext portal controller context
     * @return form
     */
    MutualizationForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Load vocabulary.
     *
     * @param portalControllerContext portal controller context
     * @param vocabulary              vocabulary name
     * @param filter                  search filter
     * @return JSON array
     */
    JSONArray loadVocabulary(PortalControllerContext portalControllerContext, String vocabulary, String filter) throws PortletException, IOException;


    /**
     * Enable mutualization.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     */
    void enable(PortalControllerContext portalControllerContext, MutualizationForm form) throws PortletException;


    /**
     * Disable mutualization.
     *
     * @param portalControllerContext portal controller context
     */
    void disable(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get redirection URL.
     *
     * @param portalControllerContext portal controller context
     * @return URL
     */
    String getRedirectionUrl(PortalControllerContext portalControllerContext) throws PortletException;

}
