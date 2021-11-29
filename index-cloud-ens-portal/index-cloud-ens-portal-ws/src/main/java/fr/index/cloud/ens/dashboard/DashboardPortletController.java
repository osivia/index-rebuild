/**
 * 
 */
package fr.index.cloud.ens.dashboard;

import javax.portlet.*;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.portlet.context.PortletConfigAware;
import org.springframework.web.portlet.context.PortletContextAware;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author JS Steux
 *
 */
@Controller
@RequestMapping(value = "VIEW")
public class DashboardPortletController {

    /** Portlet service. */
    @Autowired
    private DashboardService service;
    
    /** Portlet context. */
    @Autowired
    private PortletContext portletContext;


	@RenderMapping
	public String view() {
		return "view";
	}




    /**
     * Delete selected items action mapping.
     *
     * @param request  action request
     * @param response action response
     * @param identifiers selection identifiers request parameter
     * @param form     dashboard form model attribute
     */
    @ActionMapping("revoke")
    public void revoke(ActionRequest request, ActionResponse response, @RequestParam("clientId") String clientId, @ModelAttribute("dashboard") Dashboard dashboard) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.revoke(portalControllerContext,  clientId, dashboard);
    }


	 /**
     * Get dashboard form model attribute.
     * 
     * @param request portlet request
     * @param response portlet response
     * @return trash form
     * @throws PortletException
     */
    @ModelAttribute("dashboard")
    public Dashboard getDashboard(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        return this.service.getDashboard(portalControllerContext);
    }



}
