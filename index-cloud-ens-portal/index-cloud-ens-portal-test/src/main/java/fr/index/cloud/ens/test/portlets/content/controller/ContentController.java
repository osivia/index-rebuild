package fr.index.cloud.ens.test.portlets.content.controller;

import java.util.Iterator;

import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.model.Personnalization;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.cms.service.CMSSession;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.spi.NuxeoResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;

/**
 * Sample controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
public class ContentController implements PortletContextAware {
    
    Logger logger = Logger.getLogger(this.getClass());

    /** Portlet context. */
    private PortletContext portletContext;


    @Autowired
    private CMSService cmsService;


    /**
     * Constructor.
     */
    public ContentController() {
        super();
    }


    /**
     * Default render mapping.
     *
     * @param request render request
     * @param response render response
     * @param count count request parameter.
     * @return render view path
     * @throws Exception
     */
    @RenderMapping
    public String view(RenderRequest request, RenderResponse response) throws Exception {
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);
        NuxeoController ctx = new NuxeoController(portalControllerContext);

        logger.info("Contentcontroller.view");

        if (request.getRemoteUser() != null) {

            org.nuxeo.ecm.automation.client.model.Document documentRoot = (org.nuxeo.ecm.automation.client.model.Document) ctx.executeNuxeoCommand(new GetRootCommand(request.getRemoteUser()));
            
            CMSController ctrl = new CMSController(portalControllerContext);
            CMSSession session = cmsService.getCMSSession(ctrl.getCMSContext());

            UniversalID parentId = new UniversalID("nx",documentRoot.getProperties().getString("ttc:webid"));
            
            Documents results = (Documents) ((NuxeoResult) session.executeRequest(new GetChildrenRequest(parentId))).getResult();

            Iterator<org.nuxeo.ecm.automation.client.model.Document> nxIter = results.iterator();

            while (nxIter.hasNext()) {
                org.nuxeo.ecm.automation.client.model.Document doc = nxIter.next();
                if (doc.getProperties().getString("ttc:webid") != null) {

                    try {

                        logger.debug("get " + doc.getPath());
                        UniversalID id = new UniversalID("nx", doc.getProperties().getString("ttc:webid"));

                        Document connectDoc = session.getDocument(id);

                        logger.debug("get pubInfos" + doc.getPath());
                        Personnalization pubInfos = session.getPersonnalization(id);

                        logger.debug("ok " + connectDoc.getTitle());


                    } catch (Exception e) {
                        System.out.println("*** " + e.getMessage());
                    }
                }
            }

        }


        return "view";
    }


    @Override
    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;
    }

}
