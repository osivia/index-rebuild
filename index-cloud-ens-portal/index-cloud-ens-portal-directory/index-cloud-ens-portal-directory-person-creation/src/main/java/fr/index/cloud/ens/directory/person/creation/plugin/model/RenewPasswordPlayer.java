/**
 *
 */
package fr.index.cloud.ens.directory.person.creation.plugin.model;

import fr.index.cloud.ens.directory.person.renew.portlet.controller.RenewPasswordForm.RenewPasswordStep;
import fr.index.cloud.ens.directory.person.renew.portlet.controller.RenewPasswordPortletController;
import fr.index.cloud.ens.directory.person.renew.portlet.service.RenewPasswordService;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.player.INuxeoPlayerModule;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Loïc Billon
 */
@Component
public class RenewPasswordPlayer implements INuxeoPlayerModule {

    /** Forum portlet instance. */
    private static final String PORTLET_INSTANCE = "cloudens-person-renew-pwd-portlet-instance";


    /** Internationalization bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public RenewPasswordPlayer() {
        super();
    }


    /**
     * Get portlet player.
     *
     * @param documentContext Nuxeo document context
     * @return player
     */
    private Player getPortletPlayer(NuxeoDocumentContext documentContext, String mail) {
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(Locale.getDefault());

        // Nuxeo document
        Document document = documentContext.getDocument();

        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.WINDOW_PROP_URI, document.getPath());
        properties.put("osivia.title", bundle.getString("renew.password.title"));
        properties.put("osivia.hideDecorators", "1");
        properties.put("osivia.hideTitle", "1");
        properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, String.valueOf(true));
        properties.put("osivia.ajaxLink", "1");
        properties.put(RenewPasswordPortletController.VIEW_WINDOW_PROPERTY, RenewPasswordStep.PASSWORD.name());
        properties.put("mail", mail);

        // Player
        Player player = new Player();
        player.setWindowProperties(properties);
        player.setPortletInstance(PORTLET_INSTANCE);

        return player;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Player getCMSPlayer(NuxeoDocumentContext documentContext) {
        Document doc = documentContext.getDocument();

        if ("TaskDoc".equals(doc.getType())) {
            doc = documentContext.getDenormalizedDocument();
            PropertyMap procMap = doc.getProperties().getMap("nt:pi");

            if (procMap != null && RenewPasswordService.MODEL_ID.equals(procMap.get("pi:procedureModelWebId"))) {

                PropertyMap variables = (PropertyMap) procMap.get("pi:globalVariablesValues");
                String mail = (String) variables.get("mail");

                return this.getPortletPlayer(documentContext, mail);
            }
        }
        return null;
    }
}
