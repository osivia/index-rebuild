package fr.toutatice.portail.cms.nuxeo.repository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.PortalSSOAuthInterceptor;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.exception.DocumentNotFoundException;
import org.osivia.portal.api.cms.repository.BaseUserStorage;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;

import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoSatelliteConnectionProperties;

public class NuxeoUserStorage extends BaseUserStorage {
    
    
    @Override
    public RepositoryDocument reloadDocument(String internalID) throws CMSException {
        try {
//        RepositoryDocument doc = getDocuments().get(internalID);
//        if( doc == null)
//            throw new CMSException();
//        return doc.duplicate();
            
            System.out.println("fetching " + internalID);
            
            String secretKey = System.getProperty("nuxeo.secretKey");

            URI uri = NuxeoSatelliteConnectionProperties.getConnectionProperties(null).getPrivateBaseUri();

            String url = uri.toString() + "/site/automation";

            HttpAutomationClient client = new HttpAutomationClient(url, null);

            if (StringUtils.isNotEmpty(getUserRepository().getUserName())) {
                client.setRequestInterceptor(new PortalSSOAuthInterceptor(secretKey,getUserRepository().getUserName()));
            }
            Session session = client.getSession();
            
            OperationRequest query = session.newRequest("Document.Query");
            query.set("query", "SELECT * FROM Document WHERE ttc:webid = '"+internalID+"' ");            

            query.setHeader(org.nuxeo.ecm.automation.client.Constants.HEADER_NX_SCHEMAS, "*");        
        
            Documents results = (Documents) query.execute();
            
            
            if( results.size() != 1)
                throw new DocumentNotFoundException();
            
            Document nxDocument = results.get(0);
            Map<String,Object> properties = new HashMap<String, Object>();
            for(String key: nxDocument.getProperties().getKeys())   {
                properties.put(key, nxDocument.getProperties().get(key));
            }
            
            
            RepositoryDocument document = new RepositoryDocument(getUserRepository(), internalID, nxDocument.getPath().substring(nxDocument.getPath().lastIndexOf('/')+1), null, null, null, properties);
            return document;
  
        } catch(Exception e)    {
            throw new CMSException(e);
        }
    }


}
