/**
 * 
 */
package fr.index.cloud.ens.application.creation;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.osivia.portal.api.customization.CustomizationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import fr.toutatice.portail.cms.nuxeo.api.domain.AbstractPluginPortlet;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilter;

/**
 * @author Jean-Sébastien Steux
 *
 */
@Controller
public class ApplicationCreationPluginController extends AbstractPluginPortlet {


    /** Plugin name. */
	private static final String PLUGIN_NAME = "cloudens-application-creation.plugin";


    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;
    
    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;
	
	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.domain.AbstractPluginPortlet#getPluginName()
	 */
	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}



    /**
     * Constructor.
     */
    public ApplicationCreationPluginController() {
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

	
	
	/* (non-Javadoc)
	 * @see fr.toutatice.portail.cms.nuxeo.api.domain.AbstractPluginPortlet#customizeCMSProperties(org.osivia.portal.api.customization.CustomizationContext)
	 */
	@Override
	protected void customizeCMSProperties(CustomizationContext context) {
		
       // Form filters
        Map<String, FormFilter> formFilters = this.getFormFilters(context);
        formFilters.put(ApplicationCreationFormFilter.IDENTIFIER, this.applicationContext.getBean(ApplicationCreationFormFilter.class));
	}

}
