package fr.toutatice.portail.cms.nuxeo.repository;

import javax.portlet.PortletContext;

import org.apache.commons.lang3.StringUtils;
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
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.Satellite;
import org.osivia.portal.core.cms.spi.NuxeoRepository;
import org.osivia.portal.core.cms.spi.NuxeoRequest;
import org.osivia.portal.core.cms.spi.NuxeoResult;
import org.osivia.portal.core.web.IWebIdService;

import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

public class NuxeoRepositoryImpl extends BaseUserRepository implements NuxeoRepository {

    
    private  INuxeoService nuxeoService;
    
    /** Remote proxy webid marker. */
    public static final String RPXY_WID_MARKER = "_c_";
    
    
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
        
        if( !internalId.contains(RPXY_WID_MARKER))  {
            CMSPublicationInfos res = (CMSPublicationInfos) ((NuxeoResult) ((NuxeoUserStorage)super.getUserStorage()).executeCommand(createCommandContext(), new PublishInfosCommand(IWebIdService.FETCH_PATH_PREFIX + internalId))).getResult();
            return res.getDocumentPath();
        }   else    {
            // TODO : dans le cas d'un remote proxy coté Nuxeo le fetch combiné [WEB_ID_DOC]_c_[WEB_ID_SECTION]ne donne rien
            // coté nuxeo le : le ecm:mixinType = 'isRemoteProxy' du CMSPublicationInfos ne renvoie rien
            // Apparemment, il est uniquement mis à jour sur ToutaticeCoreProxyWithWorkflowFactory (publication par workflow)
            
            String[] webIds = StringUtils.splitByWholeSeparator(internalId, RPXY_WID_MARKER);
            // Remote proxy webid is same as live
            String liveWId = webIds[0];
            // Webid of section where live is published (section is parent of remote proxy)
            String sectionWId = webIds[1];            
            org.nuxeo.ecm.automation.client.model.Document document =  (org.nuxeo.ecm.automation.client.model.Document) ((NuxeoUserStorage)super.getUserStorage()).executeCommand(createCommandContext(), new FetchByWebIdCommand(liveWId)).getResult();
            return document.getPath();
        }


        


        
    }
    

}
