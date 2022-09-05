package fr.index.cloud.ens.customizer.plugin.theming;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.cms.UniversalID;
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
    public UniversalID adapt(UniversalID templateId) {

    if( templateId.getRepositoryName().equals("idx"))  {
        if( templateId.getInternalID().equals("DEFAULT_TEMPLATES_WORKSPACE"))  {
            templateId = new UniversalID("idx", "DEFAULT_TEMPLATES_USER-WORKSPACE");
        }
    }          
    
    return templateId;
    }

}
