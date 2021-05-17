package fr.index.cloud.ens.filebrowser.commons.portlet.service;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserForm;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.services.workspace.filebrowser.portlet.service.FileBrowserService;

import javax.portlet.PortletException;

/**
 * File browser portlet service interface.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserService
 */
public interface AbstractFileBrowserService extends FileBrowserService {

    /**
     * Selectors parameter.
     */
    String SELECTORS_PARAMETER = "selectors";

    /**
     * Keywords selector identifier.
     */
    String KEYWORDS_SELECTOR_ID = "keywords";
    /**
     * Document types selector identifier.
     */
    String DOCUMENT_TYPES_SELECTOR_ID = "documentTypes";
    /**
     * Levels selector identifier.
     */
    String LEVELS_SELECTOR_ID = "levels";
    /**
     * Subjects selector identifier.
     */
    String SUBJECTS_SELECTOR_ID = "subjects";
    /**
     * Computed size selector identifier.
     */
    String COMPUTED_SIZE_SELECTOR_ID = "size";
    /**
     * Computed date selector identifier.
     */
    String COMPUTED_DATE_SELECTOR_ID = "date";
    /**
     * Format selector identifier.
     */
    String FORMATS_SELECTOR_ID = "formats";
    /**
     * Shared selector identifier.
     */
    String SHAREDS_SELECTOR_ID = "shareds";

    /**
     * Search filter parameter.
     */
    String SEARCH_FILTER_PARAMETER = "search-filter";


    @Override
    AbstractFileBrowserForm getForm(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Get columns configuration URL.
     *
     * @param portalControllerContext portal controller context.
     * @return URL
     */
    String getColumnsConfigurationUrl(PortalControllerContext portalControllerContext) throws PortletException;


    /**
     * Reset search.
     *
     * @param portalControllerContext portal controller context
     * @param form                    form
     */
    void resetSearch(PortalControllerContext portalControllerContext, AbstractFileBrowserForm form) throws PortletException;

}
