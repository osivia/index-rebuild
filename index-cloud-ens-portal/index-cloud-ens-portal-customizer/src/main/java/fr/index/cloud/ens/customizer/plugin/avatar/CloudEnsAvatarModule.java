package fr.index.cloud.ens.customizer.plugin.avatar;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.avatar.AvatarModule;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;

import javax.portlet.PortletContext;

/**
 * Avatar module.
 *
 * @author Cédric Krommenhoek
 * @see AvatarModule
 */
public class CloudEnsAvatarModule implements AvatarModule {

    /**
     * Default avatar URL.
     */
    private static final String DEFAULT_URL = "/index-cloud-ens-charte/img/icons/user.svg";

    /**
     * Avatar Nuxeo document property.
     */
    private static final String AVATAR_DOCUMENT_PROPERTY = "userprofile:avatar";


    /**
     * Portlet context.
     */
    private final PortletContext portletContext;


    /**
     * Constructor.
     *
     * @param portletContext portlet context
     */
    public CloudEnsAvatarModule(PortletContext portletContext) {
        super();
        this.portletContext = portletContext;
    }


    @Override
    public String getUrl(String username) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(this.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT);

        // Avatar URL
        String url;

        if (StringUtils.isEmpty(username)) {
            url = null;
        } else {
            // Nuxeo command
            INuxeoCommand command = new GetUserProfileCommand(username);

            // User profile Nuxeo document
            Document userProfile = (Document) nuxeoController.executeNuxeoCommand(command);

            if ((userProfile != null) && (userProfile.getProperties().get(AVATAR_DOCUMENT_PROPERTY) == null)) {
                url = DEFAULT_URL;
            } else {
                url = null;
            }
        }

        return url;
    }

}
