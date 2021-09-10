package fr.index.cloud.ens.search.eraser.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.page.PageProperties;
import org.springframework.stereotype.Service;

import javax.portlet.ActionResponse;

/**
 * Search eraser portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonServiceImpl
 * @see SearchEraserService
 */
@Service
public class SearchEraserServiceImpl extends SearchCommonServiceImpl implements SearchEraserService {

    /**
     * Constructor.
     */
    public SearchEraserServiceImpl() {
        super();
    }


    @Override
    public void reset(PortalControllerContext portalControllerContext) {
        // Action response
        ActionResponse response = (ActionResponse) portalControllerContext.getResponse();

        response.setRenderParameter(SELECTORS_PARAMETER, StringUtils.EMPTY);

        // Refresh other portlet model attributes
        PageProperties.getProperties().setRefreshingPage(true);
    }

}
