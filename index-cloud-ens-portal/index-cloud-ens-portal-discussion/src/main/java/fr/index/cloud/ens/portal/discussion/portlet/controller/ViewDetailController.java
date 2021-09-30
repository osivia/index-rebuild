package fr.index.cloud.ens.portal.discussion.portlet.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.path.PortletPathItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fr.index.cloud.ens.portal.discussion.portlet.model.DetailForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;
import fr.index.cloud.ens.portal.discussion.portlet.model.Options;
import fr.index.cloud.ens.portal.discussion.portlet.service.DiscussionService;
import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;

/**
 * View discussion portlet controller.
 *
 * @author Jean-Sébastien Steux
 * @see CMSPortlet
 */
@Controller
@RequestMapping(path="VIEW", params = "view=detail")

public class ViewDetailController extends CMSPortlet {


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
    public ViewDetailController() {
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
    public String view(RenderRequest request, RenderResponse response,  @ModelAttribute("detailForm") DetailForm form) throws PortletException {
        
        List<PortletPathItem> pathItems = new ArrayList<PortletPathItem>();
        
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());
        
        
        // Set root breadcrumb
        Map<String, String> rootRenderParams = new HashMap<String, String>();
        
        if (!Options.MODE_ADMIN.equals(form.getOptions().getMode())) {
            PortletPathItem rootPpi = new PortletPathItem(rootRenderParams, bundle.getString("DISCUSSION_DISCUSSIONS_TITLE_ALL"));
            pathItems.add(rootPpi);
        }
      
        // Set current discussion breadcrumb
        Map<String, String> discussionRenderParams = new HashMap<String, String>();
        
        discussionRenderParams.put("view", "detail");
        if( form.getId() != null)
            discussionRenderParams.put("id", form.getId());
        if( form.getOptions().getParticipant() != null)
            discussionRenderParams.put("participant", form.getOptions().getParticipant());
        if( form.getOptions().getPublicationId() != null)
            discussionRenderParams.put("publicationId", form.getOptions().getPublicationId());     
        if( form.getOptions().getMessageId() != null)
            discussionRenderParams.put("messageId", form.getOptions().getMessageId());           
        if( form.getOptions().getMode() != null)
            discussionRenderParams.put("mode", form.getOptions().getMode());            
        
        String title = form.getDocument().getTitle() ;
        
        
                   
        if (Options.MODE_ADMIN.equals(form.getOptions().getMode())) {
            title = title + " - " + bundle.getString("DISCUSSION_MODE_ADMIN") ;
        }
         
        PortletPathItem discPpi = new PortletPathItem(discussionRenderParams, title);
        pathItems.add(discPpi);
        
        request.setAttribute(Constants.PORTLET_ATTR_PORTLET_PATH, pathItems);

        return "detail";
    }

    
    /**
     * Get detail form model attribute.
     *
     * @param request portlet request
     * @param response portlet response
     * @return discussion form
     */
    @ModelAttribute("detailForm")
    public DetailForm getDiscussionsForm(PortletRequest request, PortletResponse response, @RequestParam(name = "mode", required = false) String mode, @RequestParam(name = "id", required = false) String id, @RequestParam(name = "participant", required = false) String participant, @RequestParam(name = "publicationId", required = false) String publicationId, @RequestParam(name = "messageId", required = false) String messageId) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.service.getDetailForm(portalControllerContext, mode, id, participant, publicationId, messageId);
    }


    /**
     * Submit creation action mapping.
     * 
     * @param request action request
     * @param response action response
     * @param form form model attribute
     * @param result binding result
     * @param sessionStatus session status
     * @throws PortletException
     */
    @ActionMapping("addMessage")
    public void addMessage(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("detailForm") DetailForm form, BindingResult result,
            SessionStatus sessionStatus) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        this.service.addMessage(portalControllerContext, form);

        if (result.hasErrors()) {
            response.setRenderParameter("view", "detail");
            response.setRenderParameter("id", form.getId());
        } else {
            response.setRenderParameter("view", "detail");
            response.setRenderParameter("id", form.getId());
        }
    }

    
    /**
     * Delete current message
     *
     * @param request action request
     * @param response action response
     * @param form detail form model attribute
     */
    @ActionMapping("deleteMessage")
    public void deleteMessage(ActionRequest request, ActionResponse response, @RequestParam("messageIndice") String messageId,
            @ModelAttribute("detailForm") DetailForm form, BindingResult result) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.deleteMessage(portalControllerContext, form, messageId);
        
        if (result.hasErrors()) {
            response.setRenderParameter("view", "detail");
            response.setRenderParameter("id", form.getId());
        } else {

            response.setRenderParameter("view", "detail");
            response.setRenderParameter("id", form.getId());
        }
        
    }
    
    
    /**
     * Delete current message
     *
     * @param request action request
     * @param response action response
     * @param form detail form model attribute
     */
    @ActionMapping("reportMessage")
    public void reportMessage(ActionRequest request, ActionResponse response, @RequestParam("messageIndice") String messageId,
            @ModelAttribute("detailForm") DetailForm form, BindingResult result) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.reportMessage(portalControllerContext, form, messageId);
        
        if (result.hasErrors()) {
            response.setRenderParameter("view", "detail");
            response.setRenderParameter("id", form.getId());
        } else {

            response.setRenderParameter("view", "detail");
            response.setRenderParameter("id", form.getId());
        }
        
    }
    

    /**
     * Delete current message
     *
     * @param request action request
     * @param response action response
     * @param form detail form model attribute
     */
    @ActionMapping("deleteDiscussion")
    public void deleteDiscussion(ActionRequest request, ActionResponse response,  @ModelAttribute("detailForm") DetailForm form) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.deleteDiscussion(portalControllerContext, form);
    }
    

    
    
    /**
     * Exception handler.
     *
     * @param request portlet request
     * @param response portlet response
     * @param exception current exception
     * @return error path
     */
    @ExceptionHandler(value = Exception.class)
    public String handleException(PortletRequest request, PortletResponse response, Exception exception) {
        // Error message
        request.setAttribute("exception", exception);

        return "error";
    }
    

}
