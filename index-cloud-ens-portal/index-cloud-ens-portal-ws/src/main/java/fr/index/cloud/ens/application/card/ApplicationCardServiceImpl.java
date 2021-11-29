package fr.index.cloud.ens.application.card;

import javax.naming.Name;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.directory.v2.service.RoleService;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import fr.index.cloud.ens.application.api.Application;
import fr.index.cloud.ens.application.api.IApplicationService;
import fr.index.cloud.ens.ext.etb.EtablissementService;
import fr.index.cloud.ens.ws.DriveRestController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

/**
 * Application card portlet service implementation.
 * 
 * @see ApplicationCardService
 */
@Service
public class ApplicationCardServiceImpl implements ApplicationCardService {

    private static final String ROLE_ADMIN = "role_admin";

    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;

    /** Person service. */
    @Autowired
    private PersonService personService;

    /** Role service. */
    @Autowired
    private RoleService roleService;


    /** Plugin service. */
    @Autowired
    private IApplicationService applicationsService;


    /**
     * Constructor.
     */
    public ApplicationCardServiceImpl() {
        super();
    }

    /**
     * Gets the nuxeo controller.
     *
     * @return the nuxeo controller
     */
    private NuxeoController getNuxeoController() {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationCard getApplicationCard(PortalControllerContext portalControllerContext) throws PortletException {
        // Edition form
        ApplicationCard card = this.applicationContext.getBean(ApplicationCard.class);

        Application application = applicationsService.getApplication(this.getOptions(portalControllerContext).getAppId());
        
        
        // May have been deleted
        if( application != null)    {
            card.setCode(application.getCode().substring(EtablissementService.APPLICATION_ID_PREFIX.length()));
            card.setTitle(application.getTitle());
            card.setDescription((String) application.getDescription());
        }   

        return card;
    }


    /**
     * Check if the application card can be edited by the current user.
     *
     * @param portalControllerContext portal controller context
     * @return true if the application card is editable
     */
    protected boolean isEditable(PortalControllerContext portalControllerContext) {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();

        // Current user UID
        String uid = request.getRemoteUser();

        // Editable application indicator
        boolean editable;
        if (StringUtils.isEmpty(uid)) {
            editable = false;
        } else {
            // Current user DN
            Name dn = this.personService.getEmptyPerson().buildDn(uid);
            // Check if the current user has administration role
            editable = this.roleService.hasRole(dn, ROLE_ADMIN);
        }

        return editable;
    }


    @Override
    public void saveApplication(PortalControllerContext portalControllerContext, ApplicationCardForm form) throws PortletException {

        Application application = new Application(this.getOptions(portalControllerContext).getAppId(), form.getBean().getTitle());
        application.setDescription(form.getBean().getDescription());
        applicationsService.update(application);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationCardOptions getOptions(PortalControllerContext portalControllerContext) throws PortletException {

        ApplicationCardOptions options = this.applicationContext.getBean(ApplicationCardOptions.class);

        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        // Editable application card indicator
        boolean editable = this.isEditable(portalControllerContext);
        options.setEditable(editable);

        String id = window.getProperty("applicationId");
        options.setAppId(id);

        return options;
    }


    @Override
    public void deleteApplication(PortalControllerContext portalControllerContext, ApplicationCardOptions options) throws PortletException {
        applicationsService.deleteApplication(options.getAppId());

    }


}
