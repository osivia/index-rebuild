package fr.index.cloud.ens.portal.discussion.unread.configuration.controller;

import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsFormSort;
import fr.index.cloud.ens.portal.discussion.portlet.repository.DiscussionRepository;
import fr.index.cloud.ens.portal.discussion.portlet.service.DiscussionService;
import fr.index.cloud.ens.portal.discussion.unread.configuration.model.DiscussionsUnreadMessages;
import fr.index.cloud.ens.portal.discussion.unread.configuration.services.UnreadService;
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
 * View unread messages portlet controller.
 *
 * @author Jean-Sébastien Steux
 * @see CMSPortlet
 */
@Controller
@RequestMapping("VIEW")
public class ViewUnreadMessagesController extends CMSPortlet {


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
    private UnreadService service;

    /**
     * Person service.
     */
    @Autowired
    public PersonService personService;
    

    /**
     * Constructor.
     */
    public ViewUnreadMessagesController() {
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
        
         return "view";
    }

  
    
    /**
     * Get discussions form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return discussions form
     */
    @ModelAttribute("unreadMessages")
    public DiscussionsUnreadMessages getUnreadMessages(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.service.getDiscussionsUnreadMessages(portalControllerContext);
    }


 
}
