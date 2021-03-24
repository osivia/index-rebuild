package fr.toutatice.portail.cms.nuxeo.service.commands;

import java.util.HashMap;


import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.ecm.EcmCommand;
import org.osivia.portal.api.ecm.EcmCommonCommands;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;


public class EraseModificationsCommand extends EcmCommand {

    private INotificationsService notifService;

    private IInternationalizationService itlzService;

    public EraseModificationsCommand(INotificationsService notifService, IInternationalizationService itlzService, boolean skipCreateVersion, boolean skipCheckout) {
        super(EcmCommonCommands.eraseModifications.toString(), ReloadAfterCommandStrategy.REFRESH_NAVIGATION, "Document.EraseModifications",
                new HashMap<String, Object>());
        getRealCommandParameters().put("skipCreateVersion", skipCreateVersion);
        getRealCommandParameters().put("skipCheckout", skipCheckout);

        this.itlzService = itlzService;
        this.notifService = notifService;
    }

    public EraseModificationsCommand(INotificationsService notifService, IInternationalizationService itlzService) {
        super(EcmCommonCommands.eraseModifications.toString(), ReloadAfterCommandStrategy.REFRESH_NAVIGATION, "Document.EraseModifications",
                new HashMap<String, Object>());
        getRealCommandParameters().put("skipCreateVersion", true);// par defaut, on ne créera pas de nouvelle version
        getRealCommandParameters().put("skipCheckout", true);// par defaut, on se contentera d'initialiser la version de travail

        this.itlzService = itlzService;
        this.notifService = notifService;
    }


    @Override
    public void notifyAfterCommand(PortalControllerContext pcc) {
        String success = itlzService.getString("SUCCESS_MESSAGE_ERASE", pcc.getHttpServletRequest().getLocale());


        notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
    }

}
