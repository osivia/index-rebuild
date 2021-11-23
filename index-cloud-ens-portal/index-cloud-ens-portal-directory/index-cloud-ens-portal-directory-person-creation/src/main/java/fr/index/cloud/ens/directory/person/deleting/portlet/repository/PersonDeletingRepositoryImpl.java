package fr.index.cloud.ens.directory.person.deleting.portlet.repository;

import fr.index.cloud.ens.directory.person.deleting.portlet.repository.command.DeleteWorkspaceCommand;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import javax.portlet.PortletException;

/**
 * Person deleting portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see PersonDeletingRepository
 */
@Repository
public class PersonDeletingRepositoryImpl implements PersonDeletingRepository {

    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * CMS service locator.
     */
    @Autowired
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public PersonDeletingRepositoryImpl() {
        super();
    }


    @Override
    public boolean deleteUserWorkspace(PortalControllerContext portalControllerContext) throws PortletException {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        nuxeoController.setAsynchronousCommand(true);

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = nuxeoController.getCMSCtx();


        // User workspace path
        String userWorkspacePath;
        try {
            CMSItem userWorkspace = cmsService.getUserWorkspace(cmsContext);
            if (userWorkspace == null) {
                userWorkspacePath = null;
            } else {
                userWorkspacePath = userWorkspace.getCmsPath();
            }
        } catch (CMSException e) {
            throw new PortletException(e);
        }

        // Deleted user workspace indicator
        boolean deleted;
        if (StringUtils.isEmpty(userWorkspacePath)) {
            deleted = false;
        } else {
            // Nuxeo command
            INuxeoCommand command = this.applicationContext.getBean(DeleteWorkspaceCommand.class, userWorkspacePath);
            nuxeoController.executeNuxeoCommand(command);

            deleted = true;
        }

        return deleted;
    }

}
