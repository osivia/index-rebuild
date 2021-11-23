	/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSBinaryContent;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import fr.index.cloud.ens.directory.person.export.portlet.service.PersonExportService;
import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;

/**
 * @author Loïc Billon
 *
 */
@Controller
@RequestMapping(value = "VIEW")

public class PersonExportPortletController extends CMSPortlet {
	
    /** Portlet context. */
    @Autowired
    private PortletContext portletContext;    
    
    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;
    
    @Autowired
    private PersonUpdateService personService;
    
    @Autowired
    private PersonExportService service;
    
	
	@RenderMapping
	public String view(RenderRequest request, RenderResponse response) {
				
		return "view";
	}

	@ActionMapping(name = "exportData")
	public void exportData(ActionRequest request, ActionResponse response, SessionStatus status, @ModelAttribute("form") PersonExportForm form) throws CMSException, ParseException, PortalException {
		
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        // Get logger person
        Person person = (Person) request.getAttribute(Constants.ATTR_LOGGED_PERSON_2);
        
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(request, response, this.getPortletContext());
        CMSServiceCtx cmsCtx = nuxeoController.getCMSCtx();
        CMSItem userWorkspace = getCMSService().getUserWorkspace(cmsCtx);
        String userWorkspacePath = userWorkspace.getNavigationPath();
        
        service.prepareBatch(portalControllerContext, person.getUid(), userWorkspacePath, form);
        status.setComplete();
        
        Bundle bundle = getBundleFactory().getBundle(request.getLocale());
        String message = bundle.getString("exportaccount.notif.launched");
        getNotificationsService().addSimpleNotification(portalControllerContext, message, NotificationsType.INFO);
	}
	
	@ActionMapping(name = "remove")
	public void remove(ActionRequest request, ActionResponse response, @ModelAttribute("form") PersonExportForm form, @RequestParam("uuid") String uuid,
			SessionStatus status) throws CMSException, ParseException, PortalException {
		
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        Export export = form.getExports().get(uuid);
        
        service.remove(portalControllerContext, export, form);
        status.setComplete();
        
        Bundle bundle = getBundleFactory().getBundle(request.getLocale());
        String message = bundle.getString("exportaccount.notif.removed");
        getNotificationsService().addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);
	}

	@ModelAttribute("form")
	public PersonExportForm getForm(PortletRequest request, PortletResponse response) {
		
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);
        
        return service.getForm(portalControllerContext);
	}

    @ResourceMapping("download")
    public void download(ResourceRequest request, ResourceResponse response, @RequestParam("file") String file) throws PortletException, IOException {
		
        // Content type
        response.setContentType("application/zip");
        // Character encoding
        response.setCharacterEncoding(CharEncoding.UTF_8);
        // No cache
        response.getCacheControl().setExpirationTime(0);
        // Buffer size
        response.setBufferSize(4096);
        
        response.setProperty("Content-Disposition", getHeaderContentDisposition());

        // Input steam
        InputStream inputSteam = new FileInputStream(new File(file));
        // Output stream
        OutputStream outputStream = response.getPortletOutputStream();
        // Copy
        IOUtils.copy(inputSteam, outputStream);
        outputStream.close();
    }
    
    
    /**
     * Get header content disposition value.
     *
     * @param request HTTP servlet request
     * @param content CMS binary content
     * @param forceDownload force the download 
     * @return content disposition
     */
    private String getHeaderContentDisposition() {
       StringBuilder builder = new StringBuilder();

       // Force download
        builder.append("attachment; ");

        builder.append("filename=\"");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        builder.append("export-cloud-"+format.format(date)+".zip");
        builder.append("\"");
        return builder.toString();
    }
    
	
    /**
     * Post-construct.
     *
     * @throws PortletException
     */
    @PostConstruct
    public void postConstruct() throws PortletException {
        super.init(this.portletConfig);
    }
    
    
}
