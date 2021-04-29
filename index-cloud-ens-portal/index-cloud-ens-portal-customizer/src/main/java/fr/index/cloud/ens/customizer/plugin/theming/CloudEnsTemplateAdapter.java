package fr.index.cloud.ens.customizer.plugin.theming;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.theming.TemplateAdapter;

/**
 * Template adapter.
 *
 * @see TemplateAdapter
 */
public class CloudEnsTemplateAdapter implements TemplateAdapter {

    public CloudEnsTemplateAdapter() {
        super();
    }


    @Override
    public String adapt(String spacePath, String path, String spaceTemplate, String targetTemplate) {
        // Adapted template
        String template = null;

        // User workspace
        if (StringUtils.startsWith(spacePath, "/default-domain/UserWorkspaces/")) {
            if (StringUtils.equals(targetTemplate, "/default/templates/workspace")) {
                template = "/default/templates/user-workspace";
            }
        }

        return template;
    }

}
