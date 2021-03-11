package fr.index.cloud.ens.test.portlets.content.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.PortalSSOAuthInterceptor;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.CMSContext;
import org.osivia.portal.api.cms.CMSController;
import org.osivia.portal.api.cms.UniversalID;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.service.CMSService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.dynamic.IDynamicService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletContextAware;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoSatelliteConnectionProperties;

/**
 * Sample controller.
 *
 * @author Jean-Sébastien Steux
 */
@Controller
@RequestMapping(value = "VIEW")
public class ContentController implements PortletContextAware {

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


        String secretKey = System.getProperty("nuxeo.secretKey");

        URI uri = NuxeoSatelliteConnectionProperties.getConnectionProperties(null).getPrivateBaseUri();

        String url = uri.toString() + "/site/automation";

        HttpAutomationClient client = new HttpAutomationClient(url, null);

        if (request.getRemoteUser() != null) {
            client.setRequestInterceptor(new PortalSSOAuthInterceptor(secretKey, request.getRemoteUser()));
        }
        // Move to command pattern
        Session session = client.getSession();

        OperationRequest query = session.newRequest("Document.Query");
        query.set("query", "SELECT * FROM Document ");

        query.setHeader(org.nuxeo.ecm.automation.client.Constants.HEADER_NX_SCHEMAS, "*");

        Documents results = (Documents) query.execute();

        // TODO : move to nuxeocontroller
        CMSController ctrl = new CMSController(portalControllerContext);


        Iterator<org.nuxeo.ecm.automation.client.model.Document> nxIter = results.iterator();
        int readDocs=0;
        while (nxIter.hasNext() && readDocs < 10) {
            org.nuxeo.ecm.automation.client.model.Document doc = nxIter.next();
            if (doc.getProperties().getString("ttc:webid") != null) {
                
                try {
                UniversalID id = new UniversalID("nx", doc.getProperties().getString("ttc:webid"));

                readDocs++;
                Document connectDoc = cmsService.getCMSSession(ctrl.getCMSContext()).getDocument(id);
                
                System.out.println(connectDoc.getTitle());
                } catch( Exception e)   {
                   System.out.println("*** "+ e.getMessage());
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
