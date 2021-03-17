package org.osivia.portal.core.cms.spi;

import org.osivia.portal.api.cms.service.Request;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;

/**
 * The Class NuxeoRequest.
 */
public class NuxeoRequest implements Request {
    
    private final String repositoryName;
    private final INuxeoCommand command;
    
    /**
     * Instantiates a new parent request.
     *
     * @param parentId the parent id
     */
    public NuxeoRequest( String repositoryName, INuxeoCommand command) {
        super();
        this.repositoryName = repositoryName;
        this.command = command;
    }


       
    
    @Override
    public String getRepositoryName() {
        return repositoryName;
    }
    
    

    public INuxeoCommand getCommand() {
        return command;
    }

}
