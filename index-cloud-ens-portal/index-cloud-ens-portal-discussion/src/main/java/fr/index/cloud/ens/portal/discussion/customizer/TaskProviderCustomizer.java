package fr.index.cloud.ens.portal.discussion.customizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.CustomizationModuleMetadatas;
import org.osivia.portal.api.customization.ICustomizationModule;
import org.osivia.portal.api.customization.ICustomizationModulesRepository;
import org.osivia.portal.api.tasks.CustomTask;
import org.osivia.portal.api.tasks.ITasksProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;
import fr.index.cloud.ens.portal.discussion.portlet.repository.DiscussionRepository;


/**
 * Attributes customizer.
 *
 * @author Jean-Sébastien Steux
 * @see GenericPortlet
 * @see ICustomizationModule
 */
@Controller
public class TaskProviderCustomizer extends GenericPortlet implements ICustomizationModule {

    /** Customizer name. */
    private static final String CUSTOMIZER_NAME = "cloud-ens.customizer.discussions";
    /** Customization modules repository attribute name. */
    private static final String ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY = "CustomizationModulesRepository";


    /** Customization module metadatas. */
    private final CustomizationModuleMetadatas metadatas;



    /** Customization modules repository. */
    private ICustomizationModulesRepository repository;

    
    /** Portlet config. */
    @Autowired
    private PortletConfig portletConfig;
    
    /**
     * Portlet repository.
     */
    @Autowired
    private DiscussionRepository discussionRepository;    
    

    /** The logger. */
    protected static Log logger = LogFactory.getLog(TaskProviderCustomizer.class);
    
    /**
     * Constructor.
     */
    public TaskProviderCustomizer() {
        super();
        this.metadatas = this.generateMetadatas();

    }


    /**
     * Generate customization module metadatas.
     *
     * @return metadatas
     */
    private final CustomizationModuleMetadatas generateMetadatas() {
        CustomizationModuleMetadatas metadatas = new CustomizationModuleMetadatas();
        metadatas.setName(CUSTOMIZER_NAME);
        metadatas.setModule(this);
        metadatas.setCustomizationIDs(Arrays.asList(ITasksProvider.CUSTOMIZER_ID));
        return metadatas;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws PortletException {
        super.init();
        this.repository = (ICustomizationModulesRepository) this.getPortletContext().getAttribute(ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY);

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

        try {
            @SuppressWarnings("unchecked")
            List<EcmDocument> tasks = (List<EcmDocument>) customizationContext.getAttributes().get(ITasksProvider.CUSTOMIZER_ATTRIBUTE_TASKS_LIST);
            
            List<CustomTask> newTasks = discussionRepository.getTasks(customizationContext.getPortalControllerContext());
            for(CustomTask task: newTasks)    {
                tasks.add(task);
            }
            
            
            logger.debug("customize");
        } catch (Exception e) {
           throw new RuntimeException( e);
        }

//        Map<String, Object> attributes = customizationContext.getAttributes();
//        String name = (String) attributes.get(IAttributesBundle.CUSTOMIZER_ATTRIBUTE_NAME);
//
//        for (IAttributesBundle bundle : this.bundles) {
//            if (bundle.getAttributeNames().contains(name)) {
//                attributes.put(IAttributesBundle.CUSTOMIZER_ATTRIBUTE_RESULT, bundle);
//                break;
//            }
//        }
    }

}
