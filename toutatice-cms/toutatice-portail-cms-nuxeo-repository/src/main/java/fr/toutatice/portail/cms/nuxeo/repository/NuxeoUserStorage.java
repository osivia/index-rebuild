package fr.toutatice.portail.cms.nuxeo.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
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
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.BaseUserStorage;
import org.osivia.portal.api.cms.repository.UserData;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.spi.NuxeoResult;
import org.osivia.portal.core.web.IWebIdService;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoServiceCommand;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoSatelliteConnectionProperties;

public class NuxeoUserStorage extends BaseUserStorage {


    private static Map<String, List<Session>> nuxeoSessions = new Hashtable();

    /**
     * Gets the user session.
     *
     * @param userName the user name
     * @return the user session
     */
    public Session getUserSession() {

        List<Session> userSessions = getUserSessions();

        Session session;
        if (userSessions.size() > 0) {
            session = userSessions.get(0);
            userSessions.remove(0);
        } else {
            /* Create session */
            String secretKey = System.getProperty("nuxeo.secretKey");

            URI uri = NuxeoSatelliteConnectionProperties.getConnectionProperties(null).getPrivateBaseUri();

            String url = uri.toString() + "/site/automation";

            HttpAutomationClient client = new HttpAutomationClient(url, null);

            if (StringUtils.isNotEmpty(getUserRepository().getUserName())) {
                client.setRequestInterceptor(new PortalSSOAuthInterceptor(secretKey, getUserRepository().getUserName()));
            }
            session = client.getSession();

        }

        return session;
    }

    /**
     * Gets the user sessions.
     *
     * @param userName the user name
     * @return the user sessions
     */
    protected List<Session> getUserSessions() {
        String userName;

        userName = getUserRepository().getUserName();
      
        if (StringUtils.isEmpty(userName)) {
            userName = "_anonymous";
        }   else    {
            userName = getUserRepository().getUserName();
            if( BaseUserRepository.SUPERUSER_NAME.equals(userName))
                userName = System.getProperty("nuxeo.superUserId");
        }


        List<Session> userSessions = nuxeoSessions.get(userName);
        if (userSessions == null) {
            userSessions = new ArrayList<>();
            nuxeoSessions.put(userName, userSessions);
        }
        return userSessions;
    }

    /**
     * Recycle session.
     *
     * @param userName the user name
     * @param session the session
     */
    public void recycleSession( Session session) {
        getUserSessions().add(session);
    }


    @Override
    public RepositoryDocument reloadDocument(String internalID) throws CMSException {


       
        Session session = getUserSession();

        try {
            // RepositoryDocument doc = getDocuments().get(internalID);
            // if( doc == null)
            // throw new CMSException();
            // return doc.duplicate();

            System.out.println(">>>> getDocument  " + internalID);


              
            Document nxDocument = (Document) ((NuxeoResult)executeCommand(new FetchByWebIdCommand(internalID))).getResult();
            
            Map<String, Object> properties = new HashMap<String, Object>();
            for (String key : nxDocument.getProperties().getKeys()) {
                properties.put(key, nxDocument.getProperties().get(key));
            }


            RepositoryDocument document = new RepositoryDocument(getUserRepository(), nxDocument, internalID,
                    nxDocument.getPath().substring(nxDocument.getPath().lastIndexOf('/') + 1), null, null, null, properties);
            
            return document;

        } catch (Exception e) {
            throw new CMSException(e);
        } finally {
            recycleSession( session);
        }
    }



    @Override
    public UserData getUserData(String internalID) throws CMSException {
        UserData res = null;

        
        System.out.println(">>>> getUserData " + internalID);
        
        res = (CMSPublicationInfos) ((NuxeoResult)executeCommand(new PublishInfosCommand(IWebIdService.FETCH_PATH_PREFIX + internalID))).getResult();
      
        return res;
    }


    /**
     * Execute command.
     *
     * @param commandCtx the command ctx
     * @param command the command
     * @return the object
     * @throws Exception the exception
     */
    public NuxeoResult executeCommand(INuxeoCommand command) throws CMSException {
        

        Session session = getUserSession();

        try {
            Object result = command.execute(session);
            return new NuxeoResult(result);
        } catch (Exception e) {
            throw new CMSException(e);
        } finally {
            recycleSession( session);
        }
    }


}
