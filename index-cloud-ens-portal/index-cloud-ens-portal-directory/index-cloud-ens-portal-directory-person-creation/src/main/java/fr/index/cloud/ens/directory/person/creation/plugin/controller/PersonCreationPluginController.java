/**
 *
 */
package fr.index.cloud.ens.directory.person.creation.plugin.controller;

import fr.index.cloud.ens.directory.person.creation.plugin.model.CreateAccountPlayer;
import fr.index.cloud.ens.directory.person.creation.plugin.model.RenewPasswordPlayer;
import fr.toutatice.portail.cms.nuxeo.api.domain.AbstractPluginPortlet;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.player.IPlayerModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import java.util.List;

/**
 * @author Loïc Billon
 */
@Controller
public class PersonCreationPluginController extends AbstractPluginPortlet {

    /** Plugin name. */
    private static final String PLUGIN_NAME = "cloudens-person-creation.plugin";


    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;


    /** Create account player. */
    @Autowired
    private CreateAccountPlayer createAccountPlayer;
    /** Renew password player. */
    @Autowired
    private RenewPasswordPlayer renewPasswordPlayer;


    /**
     * Constructor.
     */
    public PersonCreationPluginController() {
        super();
    }


    /**
     * Post-construct.
     *
     * @throws PortletException
     */
    @PostConstruct
    public void postConstruct() throws PortletException {
        super.init(this.portletConfig);
    }


    /**
     * Pre-destroy.
     */
    @PreDestroy
    public void preDestroy() {
        super.destroy();
    }


    @Override
    protected String getPluginName() {
        return PLUGIN_NAME;
    }


    @Override
    public int getOrder() {
        // After procedures
        return DEFAULT_DEPLOYMENT_ORDER + 10;
    }


    @Override
    protected void customizeCMSProperties(CustomizationContext context) {
        // Players
        List<IPlayerModule> players = this.getPlayers(context);
        players.add(0, this.createAccountPlayer);
        players.add(0, this.renewPasswordPlayer);
    }

}
