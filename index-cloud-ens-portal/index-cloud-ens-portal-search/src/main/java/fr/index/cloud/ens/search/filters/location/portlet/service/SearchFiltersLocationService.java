package fr.index.cloud.ens.search.filters.location.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonService;
import fr.index.cloud.ens.search.filters.location.portlet.model.SearchFiltersLocationForm;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * Search filters location portlet service interface.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonService
 */
public interface SearchFiltersLocationService extends SearchCommonService {

    /**
     * Portlet instance.
     */
    String PORTLET_INSTANCE = "index-cloud-ens-search-filters-location-instance";


    /**
     * Get search filters location form.
     *
     * @param portalControllerContext portal controller context
     * @return form
     */
    SearchFiltersLocationForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Browse.
     *
     * @param portalControllerContext portal controller context
     * @return JSON data
     */
    String browse(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Select location.
     *
     * @param portalControllerContext portal controller context
     * @param form                    search filters location form
     */
    void select(PortalControllerContext portalControllerContext, SearchFiltersLocationForm form) throws PortletException;

}
