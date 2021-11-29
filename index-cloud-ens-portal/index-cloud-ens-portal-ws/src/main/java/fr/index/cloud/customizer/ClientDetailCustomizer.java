
package fr.index.cloud.customizer;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.CustomizationModuleMetadatas;
import org.osivia.portal.api.customization.ICustomizationModule;
import org.osivia.portal.api.customization.ICustomizationModulesRepository;
import org.osivia.portal.api.oauth2.ClientDetail;
import org.osivia.portal.api.oauth2.IOAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import fr.index.cloud.ens.application.api.Application;
import fr.index.cloud.ens.application.api.IApplicationService;


/**
 * Exposition of the OAuth2 client .
 */


@Controller
public class ClientDetailCustomizer extends GenericPortlet implements ICustomizationModule {

    /** Customizer name. */
    private static final String CUSTOMIZER_NAME = "cloud-ens.customizer.oauth2.client";
    /** Customization modules repository attribute name. */
    private static final String ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY = "CustomizationModulesRepository";


    /** Customization modules repository. */
    private ICustomizationModulesRepository repository;

    /** Customization module metadatas. */
    private final CustomizationModuleMetadatas metadatas;
    
    /** Application service. */
    @Autowired
    private IApplicationService applicationsService;


    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;
    
    /** Application context. */
    @Autowired
    private ApplicationContext applicationContext;
    
    


    /** The logger. */
    protected static Log logger = LogFactory.getLog(ClientDetailCustomizer.class);

    /**
     * Constructor.
     */
    public ClientDetailCustomizer() {
        super();
        this.metadatas = this.generateMetadatas();
    }


    /**
     * Generate customization module metadatas.
     *
     * @return metadatas
     */
    private CustomizationModuleMetadatas generateMetadatas() {
        final CustomizationModuleMetadatas metadatas = new CustomizationModuleMetadatas();
        metadatas.setName(CUSTOMIZER_NAME);
        metadatas.setModule(this);
        metadatas.setCustomizationIDs(Arrays.asList(IOAuth2Service.CLIENT_CUSTOMIZER_ID));
        return metadatas;
    }


    

    /**
     * Post-construct.
     *
     * @throws PortletException
     */
    @PostConstruct
    public void postConstruct() throws PortletException {
        super.init(this.portletConfig);
        this.repository = (ICustomizationModulesRepository) this.getPortletContext().getAttribute(ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY);
        this.repository.register(this.metadatas);

    }


    /**
     * Pre-destroy.
     */
    @PreDestroy
    public void preDestroy() {
        super.destroy();
        this.repository.unregister(this.metadatas);        
    }
    
    
   

    /**
     * {@inheritDoc}
     */
    @Override
    public void customize(CustomizationContext customizationContext) {
        Map<String, Object> attributes = customizationContext.getAttributes();
        String key = (String) attributes.get(IOAuth2Service.CUSTOMIZER_ATTRIBUTE_CLIENT_KEY);
        
        Application application = applicationsService.getApplicationByClientID(key);
        if( application != null)
            attributes.put(IOAuth2Service.CUSTOMIZER_ATTRIBUTE_CLIENT_RESULT, new ClientDetail(key, application.getTitle()));

    }



}
