package fr.index.cloud.ens.customizer.project;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.CustomizationModuleMetadatas;
import org.osivia.portal.api.customization.ICustomizationModule;
import org.osivia.portal.api.customization.ICustomizationModulesRepository;
import org.osivia.portal.api.customization.IProjectCustomizationConfiguration;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;

import fr.toutatice.portail.cms.nuxeo.api.CMSPortlet;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;

/**
 * Project customizer.
 *
 * @author Cédric Krommenhoek
 * @see CMSPortlet
 * @see ICustomizationModule
 */
public class ProjectCustomizer extends CMSPortlet implements ICustomizationModule {

	/**
	 * Customizer name.
	 */
	private static final String CUSTOMIZER_NAME = "demo.customizer.project";
	/**
	 * Customization modules repository attribute name.
	 */
	private static final String ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY = "CustomizationModulesRepository";

	/**
	 * First connection indicator window property name.
	 */
	private static final String FIRST_CONNECTION_INDICATOR_PROPERTY = "first-connection";

	/**
	 * CGU level attribute.
	 */
	private static final String CGU_LEVEL_ATTRIBUTE = "osivia.services.cgu.level";
	/**
	 * CGU path attribute.
	 */
	private static final String CGU_PATH_ATTRIBUTE = "osivia.services.cgu.path";

	/**
	 * Marker for set up the platform (data injection)
	 */
	private static final String PLATFORM_INITIALIZED = "osivia.platform.initialized";
	private static final String INIT_INDICATOR_PROPERTY = "init-indicator";

	/**
	 * Portal URL factory.
	 */
	private final IPortalUrlFactory portalUrlFactory;

	/**
	 * Person service.
	 */
	private final PersonService personService;
	/**
	 * Internationalization bundle factory.
	 */
	private final IBundleFactory bundleFactory;

	/** User preferences service. */
	private final UserPreferencesService userPreferencesService;

	/**
	 * Customization module metadatas.
	 */
	private final CustomizationModuleMetadatas metadatas;
	/**
	 * Log.
	 */
	private final Log log;
	/**
	 * Customization modules repository.
	 */
	private ICustomizationModulesRepository repository;

	/**
	 * Constructor.
	 */
	public ProjectCustomizer() {
		super();

		// Portal URL factory
		this.portalUrlFactory = Locator.getService(IPortalUrlFactory.MBEAN_NAME,IPortalUrlFactory.class);

		// Person service
		this.personService = DirServiceFactory.getService(PersonService.class);
		// Internationalization bundle factory
		IInternationalizationService internationalizationService = Locator.getService(IInternationalizationService.MBEAN_NAME, IInternationalizationService.class);

		this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

		// User preferences service
		this.userPreferencesService = DirServiceFactory.getService(UserPreferencesService.class);

		// Logs
		this.log = LogFactory.getLog(this.getClass());

		// Customization module metadata
		this.metadatas = new CustomizationModuleMetadatas();
		this.metadatas.setName(CUSTOMIZER_NAME);
		this.metadatas.setModule(this);
		this.metadatas.setCustomizationIDs(Arrays.asList(IProjectCustomizationConfiguration.CUSTOMIZER_ID));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws PortletException {
		this.repository = (ICustomizationModulesRepository) this.getPortletContext()
				.getAttribute(ATTRIBUTE_CUSTOMIZATION_MODULES_REPOSITORY);
		this.repository.register(this.metadatas);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		this.repository.unregister(this.metadatas);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void customize(CustomizationContext customizationContext) {
		// Portal controller context
		PortalControllerContext portalControllerContext = customizationContext.getPortalControllerContext();
		// Customization attributes
		Map<String, Object> attributes = customizationContext.getAttributes();
		// Project customization configuration
		IProjectCustomizationConfiguration configuration = (IProjectCustomizationConfiguration) attributes
				.get(IProjectCustomizationConfiguration.CUSTOMIZER_ATTRIBUTE_CONFIGURATION);
		// HTTP servlet request
		HttpServletRequest servletRequest = configuration.getHttpServletRequest();
		// Principal
		Principal principal = servletRequest.getUserPrincipal();
		// Bundle
		Bundle bundle = this.bundleFactory.getBundle(customizationContext.getLocale());

		if ((principal != null)) {
            if (StringUtils.isNotEmpty(configuration.getCMSPath())) {			
				this.cguRedirection(portalControllerContext, configuration, principal, bundle);
            }

		}

	}

	/**
	 * CGU redirection.
	 *
	 * @param portalControllerContext portal controller context
	 * @param configuration           project customization configuration
	 * @param principal               user principal
	 * @param bundle                  internationalization bundle
	 */
	private void cguRedirection(PortalControllerContext portalControllerContext,
			IProjectCustomizationConfiguration configuration, Principal principal, Bundle bundle) {

		// HTTP servlet request
		HttpServletRequest servletRequest = configuration.getHttpServletRequest();
		// HTTP session
		HttpSession session = servletRequest.getSession();

		// Nuxeo controller
		NuxeoController nuxeoController = new NuxeoController(this.getPortletContext());
		nuxeoController.setServletRequest(servletRequest);

		// CGU path
		String path = System.getProperty(CGU_PATH_ATTRIBUTE);
		// Portal level
		String portalLevel = System.getProperty(CGU_LEVEL_ATTRIBUTE);

		// Is CGU defined ?
		if ((portalLevel == null) || (path == null)) {
			return;
		}

		// CGU already checked (in session) ?
		String checkedLevel = String.valueOf(session.getAttribute(CGU_LEVEL_ATTRIBUTE));
		if (StringUtils.equals(portalLevel, checkedLevel)) {
			return;
		}

		// User preferences
		UserPreferences userPreferences;
		try {
			userPreferences = this.userPreferencesService.getUserPreferences(portalControllerContext);
		} catch (PortalException e) {
			this.log.error(e.getMessage());
			userPreferences = null;
		}

		// User level
		String userLevel;
		if (userPreferences == null) {
			userLevel = null;
		} else {
			userLevel = userPreferences.getTermsOfService();
			session.setAttribute(CGU_LEVEL_ATTRIBUTE, userLevel);
		}

		if (!portalLevel.equals(userLevel)) {
			session.setAttribute("osivia.services.cgu.pathToRedirect", configuration.buildRestorableURL());

			// Window properties
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(CGU_PATH_ATTRIBUTE, path);
			properties.put(CGU_LEVEL_ATTRIBUTE, portalLevel);
			properties.put("osivia.title", bundle.getString("CGU_TITLE"));
			properties.put("osivia.hideTitle", "1");
			properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, String.valueOf(true));

			// Redirection URL
			String redirectionUrl;
			try {
				redirectionUrl = this.portalUrlFactory.getStartPortletInNewPage(portalControllerContext, "cgu",
						bundle.getString("CGU_TITLE_MINI"), "osivia-services-cgu-portailPortletInstance", properties,
						new HashMap<>());
			} catch (PortalException e) {
				throw new RuntimeException(e);
			}

			configuration.setRedirectionURL(redirectionUrl);
		}
	}

}
