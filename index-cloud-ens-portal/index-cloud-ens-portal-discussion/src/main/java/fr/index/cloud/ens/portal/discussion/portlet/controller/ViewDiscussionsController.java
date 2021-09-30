package fr.index.cloud.ens.portal.discussion.portlet.controller;

import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsFormSort;
import fr.index.cloud.ens.portal.discussion.portlet.repository.DiscussionRepository;
import fr.index.cloud.ens.portal.discussion.portlet.service.DiscussionService;
import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoServiceFactory;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.api.urls.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.annotation.PostConstruct;
import javax.naming.Name;
import javax.portlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View discussions portlet controller.
 *
 * @author Jean-Sébastien Steux
 * @see CMSPortlet
 */
@Controller
@RequestMapping("VIEW")
@SessionAttributes("discussionsForm")
public class ViewDiscussionsController extends CMSPortlet {


    /**
     * Portlet config.
     */
    @Autowired
    private PortletConfig portletConfig;

    /**
     * Portlet context.
     */
    @Autowired
    private PortletContext portletContext;

    /**
     * Portlet service.
     */
    @Autowired
    private DiscussionService service;

    /**
     * Person service.
     */
    @Autowired
    public PersonService personService;
    
    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;

    /**
     * Constructor.
     */
    public ViewDiscussionsController() {
        super();
    }


    /**
     * Post-construct.
     */
    @PostConstruct
    public void postConstruct() throws PortletException {
        super.init(this.portletConfig);
    }


    /**
     * View render mapping.
     *
     * @param request render request
     * @param response render response
     * @return view path
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws PortletException {
        
        
        List<PortletPathItem> pathItems = new ArrayList<PortletPathItem>();
        
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());
        
        
        // Set root breadcrumb
        Map<String, String> rootRenderParams = new HashMap<String, String>();
        PortletPathItem rootPpi = new PortletPathItem(rootRenderParams, bundle.getString("DISCUSSION_DISCUSSIONS_TITLE_ALL"));
        pathItems.add(rootPpi);
      

        request.setAttribute(Constants.PORTLET_ATTR_PORTLET_PATH, pathItems);

        
        return "view";
    }


    /**
     * Sort action mapping.
     *
     * @param request action request
     * @param response action response
     * @param sortId sort property identifier request parameter
     * @param alt alternative sort indicator request parameter
     * @param form discussions form model attribute
     */
    @ActionMapping("sort")
    public void sort(ActionRequest request, ActionResponse response, @RequestParam("sortId") String sortId, @RequestParam("alt") String alt,
            @ModelAttribute("discussionsForm") DiscussionsForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.sort(portalControllerContext, form, DiscussionsFormSort.fromId(sortId), BooleanUtils.toBoolean(alt));
    }


    /**
     * Delete selected items action mapping.
     *
     * @param request action request
     * @param response action response
     * @param identifiers selection identifiers request parameter
     * @param form discussions form model attribute
     */
    @ActionMapping("delete")
    public void delete(ActionRequest request, ActionResponse response, @RequestParam("identifiers") String[] identifiers,
            @ModelAttribute("discussionsForm") DiscussionsForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.deleteDiscussions(portalControllerContext, form, identifiers);
    }


    
    
    /**
     * Get discussions form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return discussions form
     */
    @ModelAttribute("discussionsForm")
    public DiscussionsForm getDiscussionsForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.service.getDiscussionsForm(portalControllerContext);
    }


    /**
     * Get toolbar resource mapping.
     *
     * @param request resource request
     * @param response resource response
     * @param indexes selected row indexes
     */
    @ResourceMapping("toolbar")
    public void getToolbar(ResourceRequest request, ResourceResponse response, @RequestParam("indexes") String indexes, @ModelAttribute("discussionsForm") DiscussionsForm form) throws PortletException, IOException {
        
        
        
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        // Toolbar
        Element toolbar = this.service.getToolbar(portalControllerContext, Arrays.asList(StringUtils.split(StringUtils.trimToEmpty(indexes), ",")), form);

        // Content type
        response.setContentType("text/html");

        // Content
        HTMLWriter htmlWriter = new HTMLWriter(response.getPortletOutputStream());
        htmlWriter.write(toolbar);
        htmlWriter.close();
    }


}
