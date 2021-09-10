package fr.index.cloud.ens.search.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonService;
import fr.index.cloud.ens.search.portlet.model.SearchForm;
import fr.index.cloud.ens.search.portlet.model.SearchWindowProperties;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * Search portlet service interface.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonService
 */
public interface SearchService extends SearchCommonService {

    /**
     * Search view window property.
     */
    String VIEW_WINDOW_PROPERTY = "osivia.search.view";


    /**
     * Get search window properties.
     *
     * @param portalControllerContext portal controller context
     * @return window properties
     */
    SearchWindowProperties getWindowProperties(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Set search window properties.
     *
     * @param portalControllerContext portal controller context
     * @param windowProperties        search window properties
     */
    void setWindowProperties(PortalControllerContext portalControllerContext, SearchWindowProperties windowProperties) throws PortletException;


    /**
     * Get search form.
     *
     * @param portalControllerContext portal controller context
     * @return form
     */
    SearchForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get view path.
     *
     * @param portalControllerContext portal controller context
     * @return path
     */
    String getViewPath(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get search options URL.
     *
     * @param portalControllerContext portal controller context
     * @return URL
     */
    String getOptionsUrl(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get search redirection URL.
     *
     * @param portalControllerContext portal controller context
     * @param form                    search form
     * @return URL
     */
    String getSearchRedirectionUrl(PortalControllerContext portalControllerContext, SearchForm form) throws PortletException;


    /**
     * Reset search.
     *
     * @param portalControllerContext portal controller context
     */
    void reset(PortalControllerContext portalControllerContext) throws PortletException;

}
