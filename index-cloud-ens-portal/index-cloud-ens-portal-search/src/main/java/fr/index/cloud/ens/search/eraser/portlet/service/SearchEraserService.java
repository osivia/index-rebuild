package fr.index.cloud.ens.search.eraser.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonService;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;

/**
 * Search eraser portlet service interface.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonService
 */
public interface SearchEraserService extends SearchCommonService {

    /**
     * Reset search.
     *
     * @param portalControllerContext portal controller context
     */
    void reset(PortalControllerContext portalControllerContext) throws PortletException;

}
