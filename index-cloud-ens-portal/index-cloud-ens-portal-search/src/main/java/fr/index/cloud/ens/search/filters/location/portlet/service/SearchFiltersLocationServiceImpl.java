package fr.index.cloud.ens.search.filters.location.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonServiceImpl;
import fr.index.cloud.ens.search.filters.location.portlet.model.SearchFiltersLocationForm;
import fr.index.cloud.ens.search.filters.location.portlet.repository.SearchFiltersLocationRepository;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.path.IBrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;

/**
 * Search filters location portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonServiceImpl
 * @see SearchFiltersLocationService
 */
@Service
public class SearchFiltersLocationServiceImpl extends SearchCommonServiceImpl implements SearchFiltersLocationService {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portlet repository.
     */
    @Autowired
    private SearchFiltersLocationRepository repository;

    /**
     * Browser service.
     */
    @Autowired
    private IBrowserService browserService;


    /**
     * Constructor.
     */
    public SearchFiltersLocationServiceImpl() {
        super();
    }


    @Override
    public SearchFiltersLocationForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        // User workspace path
        String userWorkspacePath = this.repository.getUserWorkspacePath(portalControllerContext);

        // Form
        SearchFiltersLocationForm form = this.applicationContext.getBean(SearchFiltersLocationForm.class);

        // Base path
        String basePath;
        if (StringUtils.isEmpty(userWorkspacePath)) {
            basePath = null;
        } else {
            basePath = userWorkspacePath + "/documents";
        }
        form.setBasePath(basePath);

        return form;
    }


    @Override
    public String browse(PortalControllerContext portalControllerContext) throws PortletException {
        // Data
        String data;
        try {
            data = this.browserService.browse(portalControllerContext);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return data;
    }


    @Override
    public void select(PortalControllerContext portalControllerContext, SearchFiltersLocationForm form) throws PortletException {

    }

}
