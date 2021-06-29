package fr.index.cloud.ens.mutualization.portlet.controller;

import fr.index.cloud.ens.mutualization.portlet.model.MutualizationForm;
import fr.index.cloud.ens.mutualization.portlet.model.validation.MutualizationFormValidator;
import fr.index.cloud.ens.mutualization.portlet.service.MutualizationService;
import net.sf.json.JSONArray;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Mutualization portlet controller.
 *
 * @author Cédric Krommenhoek
 */
@Controller
@RequestMapping("VIEW")
public class MutualizationController {

    /**
     * Portlet context.
     */
    @Autowired
    private PortletContext portletContext;

    /**
     * Portlet service.
     */
    @Autowired
    private MutualizationService service;

    /**
     * Form validator.
     */
    @Autowired
    private MutualizationFormValidator validator;


    /**
     * Constructor.
     */
    public MutualizationController() {
        super();
    }


    /**
     * View render mapping.
     *
     * @return view path
     */
    @RenderMapping
    public String view() {
        return "view";
    }


    /**
     * Enable mutualization.
     *
     * @param request       action request
     * @param response      action response
     * @param form          form model attribute
     * @param bindingResult binding result
     */
    @ActionMapping("enable")
    public void enable(ActionRequest request, ActionResponse response, @Validated @ModelAttribute("form") MutualizationForm form, BindingResult bindingResult) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        if (!bindingResult.hasErrors()) {
            this.service.enable(portalControllerContext, form);

            // Redirection
            String url = this.service.getRedirectionUrl(portalControllerContext);
            response.sendRedirect(url);
        }
    }


    /**
     * Disable mutualization.
     *
     * @param request  action request
     * @param response action response
     */
    @ActionMapping("disable")
    public void disable(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        this.service.disable(portalControllerContext);

        // Redirection
        String url = this.service.getRedirectionUrl(portalControllerContext);
        response.sendRedirect(url);
    }


    /**
     * Load vocabulary resource mapping.
     *
     * @param request    resource request
     * @param response   resource response
     * @param vocabulary vocabulary name request parameter
     * @param filter     select2 search filter request parameter
     */
    @ResourceMapping("load-vocabulary")
    public void loadVocabulary(ResourceRequest request, ResourceResponse response, @RequestParam("vocabulary") String vocabulary, @RequestParam(name = "filter", required = false) String filter) throws PortletException, IOException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);

        // Select2 results
        JSONArray results = this.service.loadVocabulary(portalControllerContext, vocabulary, filter);

        // Content type
        response.setContentType("application/json");

        // Content
        PrintWriter printWriter = new PrintWriter(response.getPortletOutputStream());
        printWriter.write(results.toString());
        printWriter.close();
    }


    /**
     * Get form model attribute.
     *
     * @param request  portlet request
     * @param response portlet response
     * @return form
     */
    @ModelAttribute("form")
    public MutualizationForm getForm(PortletRequest request, PortletResponse response) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.portletContext, request, response);

        return this.service.getForm(portalControllerContext);
    }


    /**
     * Form init binder.
     *
     * @param binder portlet request data binder
     */
    @InitBinder("form")
    public void formInitBinder(PortletRequestDataBinder binder) {
        binder.setDisallowedFields("enable", "suggestedKeywords");
        binder.setValidator(this.validator);
    }

}
