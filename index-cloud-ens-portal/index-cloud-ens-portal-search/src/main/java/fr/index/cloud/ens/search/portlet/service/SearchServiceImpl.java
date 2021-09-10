package fr.index.cloud.ens.search.portlet.service;

import fr.index.cloud.ens.search.common.portlet.service.SearchCommonServiceImpl;
import fr.index.cloud.ens.search.filters.home.settings.portlet.service.SearchFiltersHomeSettingsService;
import fr.index.cloud.ens.search.filters.portlet.service.SearchFiltersService;
import fr.index.cloud.ens.search.portlet.model.SearchForm;
import fr.index.cloud.ens.search.portlet.model.SearchView;
import fr.index.cloud.ens.search.portlet.model.SearchWindowProperties;
import fr.index.cloud.ens.search.portlet.repository.SearchRepository;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.PageSelectors;
import fr.toutatice.portail.cms.nuxeo.api.VocabularyHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.osivia.portal.core.page.PageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Search portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonServiceImpl
 * @see SearchService
 */
@Service
public class SearchServiceImpl extends SearchCommonServiceImpl implements SearchService {

    /**
     * Search view prefix.
     */
    private static final String VIEW_PREFIX = "view-";


    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portlet repository.
     */
    @Autowired
    private SearchRepository repository;

    /**
     * Portal URL factory.
     */
    @Autowired
    private IPortalUrlFactory portalUrlFactory;

    /**
     * Internationalization bundle factory.
     */
    @Autowired
    private IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public SearchServiceImpl() {
        super();
    }


    @Override
    public SearchWindowProperties getWindowProperties(PortalControllerContext portalControllerContext) {
        // Window
        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());

        // Window properties
        SearchWindowProperties windowProperties = this.applicationContext.getBean(SearchWindowProperties.class);

        // Search view
        SearchView view = SearchView.fromId(window.getProperty(VIEW_WINDOW_PROPERTY));
        windowProperties.setView(view);

        return windowProperties;
    }


    @Override
    public void setWindowProperties(PortalControllerContext portalControllerContext, SearchWindowProperties windowProperties) {
        // Window
        PortalWindow window = WindowFactory.getWindow(portalControllerContext.getRequest());

        // Search view
        SearchView view = windowProperties.getView();
        if (view == null) {
            view = SearchView.DEFAULT;
        }
        window.setProperty(VIEW_WINDOW_PROPERTY, view.getId());
    }


    @Override
    public SearchForm getForm(PortalControllerContext portalControllerContext) {
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        // Window properties
        SearchWindowProperties windowProperties = this.getWindowProperties(portalControllerContext);

        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Search form
        SearchForm form = this.applicationContext.getBean(SearchForm.class);

        // Search view
        SearchView view = windowProperties.getView();
        form.setView(view);

        // Search folder name
        Document document = this.repository.getDocument(portalControllerContext);
        if (document != null) {
            PropertyList facets = document.getFacets();
            if (facets.list().contains("Folderish")) {
                form.setFolderName(document.getTitle());
            }
        }

        if (SearchView.AUTOSUBMIT.equals(view)) {
            // Selectors
            Map<String, List<String>> selectors = PageSelectors.decodeProperties(request.getParameter(SELECTORS_PARAMETER));

            String keywords = getSelectorValue(selectors, KEYWORDS_SELECTOR_ID);
            form.setValue(keywords);
        }

        if (SearchView.REMINDER.equals(view)) {
            // Selectors
            Map<String, List<String>> selectors = PageSelectors.decodeProperties(request.getParameter(SELECTORS_PARAMETER));

            // Reminder
            List<String> reminder;

            if (MapUtils.isEmpty(selectors)) {
                reminder = null;
            } else {
                reminder = new ArrayList<>();

                // Displayed selectors
                List<String> displayedSelectors = Arrays.asList(LEVELS_SELECTOR_ID, SUBJECTS_SELECTOR_ID, DOCUMENT_TYPES_SELECTOR_ID, SIZE_AMOUNT_SELECTOR_ID, DATE_RANGE_SELECTOR_ID, SHAREDS_SELECTOR_ID);

                for (Map.Entry<String, List<String>> selector : selectors.entrySet()) {
                    // Selector name
                    String name = selector.getKey();
                    // Selector values
                    List<String> values = selector.getValue();

                    if (displayedSelectors.contains(name) && CollectionUtils.isNotEmpty(values)) {
                        if (SIZE_AMOUNT_SELECTOR_ID.equals(name)) {
                            String range = this.getSelectorValue(selectors, SIZE_RANGE_SELECTOR_ID);
                            String amount = this.getSelectorValue(selectors, SIZE_AMOUNT_SELECTOR_ID);
                            String unit = this.getSelectorValue(selectors, SIZE_UNIT_SELECTOR_ID);

                            if (StringUtils.isNotEmpty(range) && StringUtils.isNotEmpty(amount) && StringUtils.isNotEmpty(unit)) {
                                // Displayed value internationalization key
                                String key;
                                if ("LESS".equals(range)) {
                                    key = "SEARCH_FILTERS_REMINDER_SIZE_RANGE_LESS";
                                } else {
                                    key = "SEARCH_FILTERS_REMINDER_SIZE_RANGE_MORE";
                                }

                                // Displayed value
                                String display = bundle.getString(key, amount, bundle.getString("SEARCH_FILTERS_SIZE_UNIT_" + StringUtils.upperCase(unit)));

                                reminder.add(display);
                            }
                        } else if (DATE_RANGE_SELECTOR_ID.equals(name)) {
                            String range = this.getSelectorValue(selectors, DATE_RANGE_SELECTOR_ID);

                            // Displayed value
                            String display;

                            if (StringUtils.isEmpty(range) || "UNSET".equals(range)) {
                                display = null;
                            } else if ("CUSTOMIZED".equals(range)) {
                                String value = this.getSelectorValue(selectors, CUSTOMIZED_DATE_SELECTOR_ID);

                                // Customized date
                                Date customizedDate;
                                if (StringUtils.isEmpty(value)) {
                                    customizedDate = null;
                                } else {
                                    // Date format
                                    DateFormat dateFormat = new SimpleDateFormat(DateFormatUtils.ISO_DATE_FORMAT.getPattern());

                                    try {
                                        customizedDate = dateFormat.parse(value);
                                    } catch (ParseException e) {
                                        customizedDate = null;
                                    }
                                }

                                if (customizedDate == null) {
                                    display = null;
                                } else {
                                    display = DateFormat.getDateInstance(DateFormat.MEDIUM).format(customizedDate);
                                }
                            } else {
                                display = bundle.getString("SEARCH_FILTERS_DATE_RANGE_" + StringUtils.upperCase(range));
                            }

                            if (StringUtils.isNotEmpty(display)) {
                                reminder.add(display);
                            }
                        } else {
                            // Selector vocabulary
                            String vocabulary;
                            if (LEVELS_SELECTOR_ID.equals(name)) {
                                vocabulary = "idx_level";
                            } else if (SUBJECTS_SELECTOR_ID.equals(name)) {
                                vocabulary = "idx_subject";
                            } else if (DOCUMENT_TYPES_SELECTOR_ID.equals(name)) {
                                vocabulary = "idx_document_type";
                            } else if (FORMATS_SELECTORID.equals(name)) {
                                vocabulary = "idx_file_format";
                            } else {
                                vocabulary = null;
                            }

                            if (StringUtils.isNotEmpty(vocabulary)) {
                                for (String value : values) {
                                    // Vocabulary entry key
                                    String key;
                                    if (StringUtils.contains(value, "/")) {
                                        key = StringUtils.substringAfterLast(value, "/");
                                    } else {
                                        key = value;
                                    }

                                    // Displayed value
                                    String display = VocabularyHelper.getVocabularyLabel(nuxeoController, vocabulary, key);

                                    if (StringUtils.isNotEmpty(display)) {
                                        reminder.add(display);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            form.setReminder(reminder);
        }

        return form;
    }


    @Override
    public String getViewPath(PortalControllerContext portalControllerContext) {
        // Window properties
        SearchWindowProperties windowProperties = this.getWindowProperties(portalControllerContext);

        return VIEW_PREFIX + windowProperties.getView().getId();
    }


    @Override
    public String getOptionsUrl(PortalControllerContext portalControllerContext) throws PortletException {
        // Window properties
        SearchWindowProperties windowProperties = this.getWindowProperties(portalControllerContext);

        // Search options portlet instance;
        String instance;
        if (SearchView.HOME_SETTINGS_BUTTON.equals(windowProperties.getView())) {
            instance = SearchFiltersHomeSettingsService.PORTLET_INSTANCE;
        } else {
            instance = SearchFiltersService.PORTLET_INSTANCE;
        }

        // Search options window properties
        Map<String, String> properties = new HashMap<>();

        // URL
        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, instance, properties, PortalUrlType.MODAL);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    @Override
    public String getSearchRedirectionUrl(PortalControllerContext portalControllerContext, SearchForm form) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Action response
        ActionResponse response = (ActionResponse) portalControllerContext.getResponse();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Current window properties
        SearchWindowProperties currentWindowProperties = this.getWindowProperties(portalControllerContext);

        // Search path
        String path;
        if (SearchView.INPUT.equals(currentWindowProperties.getView())) {
            path = this.repository.getSearchPath(portalControllerContext);
        } else {
            path = null;
        }


        // Search URL
        String url;

        if (SearchView.INPUT.equals(currentWindowProperties.getView())) {
            if (StringUtils.isEmpty(path)) {
                url = null;
            } else {
                // Selectors
                Map<String, List<String>> selectors;
                if (form == null) {
                    selectors = PageSelectors.decodeProperties(request.getParameter(SELECTORS_PARAMETER));
                } else {
                    selectors = new HashMap<>();

                    // Location
                    String navigationPath = this.repository.getNavigationPath(portalControllerContext);
                    if (StringUtils.isNotEmpty(navigationPath)) {
                        selectors.put(LOCATION_SELECTOR_ID, Collections.singletonList(navigationPath));
                    }

                    // Search
                    String search = form.getValue();
                    if (StringUtils.isNotEmpty(search)) {
                        selectors.put(KEYWORDS_SELECTOR_ID, Collections.singletonList(search));
                    }
                }

                // Page parameters
                Map<String, String> parameters = new HashMap<>(1);
                parameters.put("selectors", PageParametersEncoder.encodeProperties(selectors));

                // CMS URL
                url = this.portalUrlFactory.getCMSUrl(portalControllerContext, null, path, parameters, null, null, null, null, null, null);
            }
        } else if (SearchView.AUTOSUBMIT.equals(currentWindowProperties.getView())) {
            url = null;

            // Selectors
            String selectorsParameter = request.getParameter(SELECTORS_PARAMETER);
            Map<String, List<String>> selectors = PageSelectors.decodeProperties(selectorsParameter);

            // Search
            String search;
            if (form == null) {
                search = null;
            } else {
                search = form.getValue();
            }
            if (StringUtils.isEmpty(search)) {
                selectors.remove(KEYWORDS_SELECTOR_ID);
            } else {
                selectors.put(KEYWORDS_SELECTOR_ID, Collections.singletonList(search));
            }

            response.setRenderParameter("selectors", PageSelectors.encodeProperties(selectors));

            // Refresh other portlet model attributes
            PageProperties.getProperties().setRefreshingPage(true);

            request.setAttribute("osivia.ajax.preventRefresh", Constants.PORTLET_VALUE_ACTIVATE);
        } else if (SearchView.BUTTON.equals(currentWindowProperties.getView()) || SearchView.BUTTONS_SEARCH_AND_RESET.equals(currentWindowProperties.getView())) {
            // Title internationalization key
            String titleKey;
            if (SearchView.BUTTON.equals(currentWindowProperties.getView())) {
                titleKey = "SEARCH_BUTTON_LABEL";
            } else if (SearchView.BUTTONS_SEARCH_AND_RESET.equals(currentWindowProperties.getView())) {
                titleKey = "SEARCH_BUTTONS_LABEL_1";
            } else {
                titleKey = null;
            }

            // Portlet instance
            String portletInstance = "index-cloud-ens-search-filters-instance";

            // Window properties
            Map<String, String> windowProperties = new HashMap<>();
            windowProperties.put("osivia.title", bundle.getString(titleKey));
            windowProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, String.valueOf(true));
            windowProperties.put("osivia.ajaxLink", String.valueOf(1));
            windowProperties.put("osivia.back.reset", String.valueOf(true));

            try {
                url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, portletInstance, windowProperties);
            } catch (PortalException e) {
                throw new PortletException(e);
            }
        } else {
            throw new PortletException("Unknown search view.");
        }

        return url;
    }


    @Override
    public void reset(PortalControllerContext portalControllerContext) {
        // Action response
        ActionResponse response = (ActionResponse) portalControllerContext.getResponse();

        // Reset selectors
        response.setRenderParameter(SELECTORS_PARAMETER, StringUtils.EMPTY);
        response.removePublicRenderParameter(SEARCH_FILTER_PARAMETER);

        // Refresh other portlet model attributes
        PageProperties.getProperties().setRefreshingPage(true);
    }

}
