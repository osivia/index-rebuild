package fr.toutatice.portail.cms.nuxeo.repository;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletContext;

import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.repository.BaseUserStorage;
import org.osivia.portal.api.cms.repository.UserData;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.Satellite;
import org.osivia.portal.core.cms.spi.NuxeoRepository;
import org.osivia.portal.core.cms.spi.NuxeoResult;
import org.osivia.portal.core.web.IWebIdService;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoCommandService;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoServiceCommand;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandServiceFactory;
import fr.toutatice.portail.cms.nuxeo.services.NuxeoCommandCacheInvoker;

public class NuxeoUserStorage extends BaseUserStorage {

   
    private  INuxeoService nuxeoService;
    
    private INuxeoCommandService nuxeoCommandService;
    
    private INuxeoService getNuxeoService()   {
        
        if (this.nuxeoService == null) {
            
            this.nuxeoService = Locator.findMBean(INuxeoService.class, "osivia:service=NuxeoService");

        }        
        return this.nuxeoService;
    }
    
    public INuxeoCommandService getNuxeoCommandService() throws Exception {
        if (this.nuxeoCommandService == null) {
            this.nuxeoCommandService = NuxeoCommandServiceFactory.getNuxeoCommandService(getNuxeoService().getCMSCustomizer().getPortletContext());
        }
        return this.nuxeoCommandService;
    }
    
    /**
     * Creates a default command context without cache
     *
     * @return the nuxeo command context
     */
    private NuxeoCommandContext createCommandContext( boolean superUser)  {
        
        NuxeoCommandContext commandCtx = null;

        PortletContext cmsPortletContext = getNuxeoService().getCMSCustomizer().getPortletContext();
        
        if (getUserRepository().getPortalContext() != null) {
            commandCtx = new NuxeoCommandContext(cmsPortletContext, getUserRepository().getPortalContext());
        } 

        if (commandCtx == null) {
            commandCtx = new NuxeoCommandContext(cmsPortletContext);
        }


        commandCtx.setForceReload(true);

        // Par défaut
        if( superUser)
            commandCtx.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        else
            commandCtx.setAuthType(NuxeoCommandContext.AUTH_TYPE_USER);
        commandCtx.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        
        return commandCtx;
    }

  



    @Override
    public RepositoryDocument reloadDocument(String internalID) throws CMSException {
       try {
              
            String path = ((NuxeoRepository)(getUserRepository())).getPath(internalID);
            
            Document nxDocument =  (Document) ((NuxeoResult)executeCommand(createCommandContext(true), new DocumentFetchLiveCommand(path,"Read"))).getResult();
            
            Map<String, Object> properties = new HashMap<String, Object>();
            for (String key : nxDocument.getProperties().getKeys()) {
                properties.put(key, nxDocument.getProperties().get(key));
            }


            RepositoryDocument document = new RepositoryDocument(getUserRepository(), nxDocument, internalID,
                    nxDocument.getPath().substring(nxDocument.getPath().lastIndexOf('/') + 1), null, null, null, properties);
            
            return document;

        } catch (Exception e) {
            throw new CMSException(e);
        } 
    }



    @Override
    public UserData getUserData(String internalID) throws CMSException {

        CMSPublicationInfos res = null;
      
        res = (CMSPublicationInfos) ((NuxeoResult)executeCommand(createCommandContext( false), new PublishInfosCommand(IWebIdService.FETCH_PATH_PREFIX + internalID))).getResult();
        res.setSatellite(Satellite.MAIN);
      
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
    
    public NuxeoResult executeCommand(NuxeoCommandContext nuxeoCommandContext, INuxeoCommand command) throws CMSException {
        
        try {

     
        
        Object res =  this.getNuxeoCommandService().executeCommand(nuxeoCommandContext, new INuxeoServiceCommand() {
            
            @Override
            public String getId() {
                return command.getId();
            }

            @Override
            public Object execute(Session nuxeoSession) throws Exception {
                return command.execute(nuxeoSession);
            }
        });
        
        
        return new NuxeoResult(res);
        
     
        } catch (Exception e) {
            if( !(e instanceof CMSException))
                throw new CMSException(e);
            else
                throw (CMSException) e;
        } 
    }    


    
    /**
     * Execute command.
     *
     * @param command the command
     * @return the nuxeo result
     * @throws CMSException the CMS exception
     */
    public NuxeoResult executeCommand( INuxeoCommand command) throws CMSException {
        
        return executeCommand( createCommandContext(false),  command);

    }
}
