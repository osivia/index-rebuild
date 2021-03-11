package fr.toutatice.portail.cms.nuxeo.repository;

import org.osivia.portal.api.cms.exception.CMSException;
import org.osivia.portal.api.cms.repository.BaseUserRepository;

import org.osivia.portal.api.cms.repository.cache.SharedRepositoryKey;
import org.osivia.portal.api.cms.service.NativeRepository;
import org.osivia.portal.api.cms.service.Request;
import org.osivia.portal.api.cms.service.Result;

public class NuxeoRepository extends BaseUserRepository implements NativeRepository {

    public NuxeoRepository(SharedRepositoryKey repositoryKey, BaseUserRepository publishRepository, String userName) {
        super(repositoryKey, publishRepository, userName, new NuxeoUserStorage());
    }

    @Override
    public Result executeRequest(Request request) throws CMSException {
        return null;
    }

    @Override
    public boolean supportPreview() {
        return false;
    }

    @Override
    protected void initDocuments() {
    }

}
