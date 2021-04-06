package fr.toutatice.portail.cms.nuxeo.repository;

import javax.portlet.PortletContext;

import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.repository.BaseUserRepository;
import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.spi.NuxeoRepository;
import org.osivia.portal.core.cms.spi.NuxeoRequest;


import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

public class NuxeoRepositoryImpl extends BaseUserRepository implements NuxeoRepository {

    
    private  INuxeoService nuxeoService;
    
    
    private INuxeoService getNuxeoService()   {
        
        if (this.nuxeoService == null) {
            
            this.nuxeoService = Locator.findMBean(INuxeoService.class, "osivia:service=NuxeoService");

        }        
        return this.nuxeoService;
    }
    
    public NuxeoRepositoryImpl(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName) {
        super(repositoryKey, publishRepository, userName, new NuxeoUserStorage());
    }

    @Override
    public Result executeRequest(Request request) throws CMSException {
        if( request instanceof NuxeoRequest)    {
            return ((NuxeoUserStorage) super.getUserStorage()).executeCommand(((NuxeoRequest) request).getCommandContext(), ((NuxeoRequest) request).getCommand());
        } else if (request instanceof GetChildrenRequest)   {
            Document parent = getDocument(((GetChildrenRequest) request).getParentId().getInternalID());
            org.nuxeo.ecm.automation.client.model.Document nuxeoDoc = (org.nuxeo.ecm.automation.client.model.Document)  parent.getNativeItem();
            return ((NuxeoUserStorage) super.getUserStorage()).executeCommand(new GetChildrenCommand(nuxeoDoc.getId()));
         }
            
        throw new CMSException(new IllegalArgumentException("Request not implemented"));
    }

  
    
    @Override
    public boolean supportPreview() {
        return false;
    }

    @Override
    protected void initDocuments() {
    }

    
    
    
    /**
     * Creates a default command context without cache
     *
     * @return the nuxeo command context
     */
    private NuxeoCommandContext createCommandContext( )  {
        
        NuxeoCommandContext commandCtx = null;

        PortletContext cmsPortletContext = getNuxeoService().getCMSCustomizer().getPortletContext();
        
        commandCtx = new NuxeoCommandContext(cmsPortletContext);
        commandCtx.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        commandCtx.setCacheType(CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT);
        
        return commandCtx;
    }
    
    @Override
    public String getInternalId( String path) throws CMSException   {

        // Get result by cache pattern
        // TODO : update if the document is modified        
        org.nuxeo.ecm.automation.client.model.Document document =  (org.nuxeo.ecm.automation.client.model.Document) ((NuxeoUserStorage)super.getUserStorage()).executeCommand(createCommandContext(), new FetchByPathCommand(path)).getResult();
      
        return (String) document.getString("ttc:webid");
    }

    @Override
    public String getPath( String internalId) throws CMSException   {

        // Get result by cache pattern
        // TODO : update if the document is modified
        
        org.nuxeo.ecm.automation.client.model.Document document =  (org.nuxeo.ecm.automation.client.model.Document) ((NuxeoUserStorage)super.getUserStorage()).executeCommand(createCommandContext(), new FetchByWebIdCommand(internalId)).getResult();
        return  document.getPath();
    }
    

}
