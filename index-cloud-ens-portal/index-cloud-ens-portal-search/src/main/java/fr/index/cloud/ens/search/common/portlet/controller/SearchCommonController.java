package fr.index.cloud.ens.search.common.portlet.controller;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonService;
import fr.index.cloud.ens.search.common.portlet.model.SearchFiltersVocabulary;
import net.sf.json.JSONArray;

import org.apache.commons.lang.BooleanUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class SearchCommonController {

    /**
     * Portlet context.
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private PortletContext portletContext;

    /**
     * Portlet service.
     */
    @Autowired
    private SearchCommonService service;


    /**
     * Load levels select2 vocabulary resource mapping.
     *
     * @param request        resource request
     * @param response       resource response
     * @param vocabularyName vocabulary name request parameter
     * @param filter         select2 filter request parameter
     */
    @ResourceMapping("load-vocabulary")
    public void loadVocabulary(ResourceRequest request, ResourceResponse response, @RequestParam("vocabulary") String vocabularyName, @RequestParam(name = "filter", required = false) String filter, @RequestParam(name = "addAll", required = false) String addAll) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        // Vocabulary
        SearchFiltersVocabulary vocabulary = SearchFiltersVocabulary.fromVocabularyName(vocabularyName);
        
        
        
        // Select2 results
        Boolean bAddAll = BooleanUtils.toBoolean(addAll);
        JSONArray results = this.service.loadVocabulary(portalControllerContext, vocabulary, filter, bAddAll);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(results.toString());
        printWriter.close();
    }

}
