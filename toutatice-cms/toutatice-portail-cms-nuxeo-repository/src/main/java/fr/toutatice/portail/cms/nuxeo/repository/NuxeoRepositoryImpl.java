package fr.toutatice.portail.cms.nuxeo.repository;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.model.Document;
import org.osivia.portal.api.cms.repository.BaseUserRepository;

import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.repository.model.shared.RepositoryDocument;
import org.osivia.portal.api.cms.service.GetChildrenRequest;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;
import org.osivia.portal.core.cms.spi.NuxeoRepository;
import org.osivia.portal.core.cms.spi.NuxeoRequest;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

public class NuxeoRepositoryImpl extends BaseUserRepository implements NuxeoRepository {

    public NuxeoRepositoryImpl(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName) {
        super(repositoryKey, publishRepository, userName, new NuxeoUserStorage());
    }

    @Override
    public Result executeRequest(Request request) throws CMSException {
        if( request instanceof NuxeoRequest)    {
            return ((NuxeoUserStorage) super.getUserStorage()).executeCommand(((NuxeoRequest) request).getCommand());
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

    

}
