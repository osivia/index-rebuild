package fr.index.cloud.ens.portal.discussion.unread.configuration.services;

import org.dom4j.Element;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.panels.PanelPlayer;

import fr.index.cloud.ens.portal.discussion.portlet.model.DetailForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionCreation;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsForm;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsFormSort;
import fr.index.cloud.ens.portal.discussion.unread.configuration.model.DiscussionsUnreadMessages;

import javax.portlet.PortletException;
import java.io.IOException;
import java.util.List;

/**
 * Discussion service interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface UnreadService {

 
    /**
     * Gets the discussions unread messages.
     *
     * @param portalControllerContext the portal controller context
     * @return the discussions unread messages
     * @throws PortletException the portlet exception
     */
    DiscussionsUnreadMessages getDiscussionsUnreadMessages(PortalControllerContext portalControllerContext) throws PortletException;





}
