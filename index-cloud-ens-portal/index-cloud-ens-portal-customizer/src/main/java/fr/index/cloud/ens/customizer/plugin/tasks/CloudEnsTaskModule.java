package fr.index.cloud.ens.customizer.plugin.tasks;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.tasks.TaskModule;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Customized task module.
 *
 * @author Cédric Krommenhoek
 * @see TaskModule
 */
public class CloudEnsTaskModule implements TaskModule {

    /** Uncluttered model webIds. */
    private final List<String> unclutteredModelWebIds;


    /**
     * Constructor.
     */
    public CloudEnsTaskModule() {
        super();

        // Uncluttered model webIds
        this.unclutteredModelWebIds = Arrays.asList("procedure_person-creation-pronote", "procedure_renew-password");
    }


    @Override
    public void adaptTaskItem(PortalControllerContext portalControllerContext, EcmDocument ecmDocument, Map<String, String> properties) throws PortalException {
        if (ecmDocument instanceof Document) {
            // Nuxeo document
            Document nuxeoDocument = (Document) ecmDocument;

            // Procedure instance properties
            PropertyMap procedureInstance = nuxeoDocument.getProperties().getMap("nt:pi");
            // Model webId
            String modelWebId = procedureInstance.getString("pi:procedureModelWebId");

            if (this.unclutteredModelWebIds.contains(modelWebId)) {
                properties.put("displayContext", "uncluttered");
            }
        }
    }

}
