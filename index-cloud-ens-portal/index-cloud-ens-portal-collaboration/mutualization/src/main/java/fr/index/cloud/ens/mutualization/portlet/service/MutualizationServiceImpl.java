package fr.index.cloud.ens.mutualization.portlet.service;

import fr.index.cloud.ens.mutualization.portlet.model.MutualizationForm;
import fr.index.cloud.ens.mutualization.portlet.model.VocabularyItem;
import fr.index.cloud.ens.mutualization.portlet.repository.MutualizationRepository;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.discussions.DiscussionHelper;
import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.cms.service.UpdateScope;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Normalizer;
import java.util.*;

/**
 * Mutualization portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see MutualizationService
 */
@Service
public class MutualizationServiceImpl implements MutualizationService {

    /**
     * Mutualized space path.
     */
    private static final String MUTUALIZED_SPACE_PATH = System.getProperty("config.mutualized.path");


    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Portlet repository.
     */
    @Autowired
    private MutualizationRepository repository;

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
     * Notifications service.
     */
    @Autowired
    private INotificationsService notificationsService;
    

    /**
     * Document DAO.
     */
    @Autowired
    private DocumentDAO documentDao;


    /**
     * Constructor.
     */
    public MutualizationServiceImpl() {
        super();
    }


    @Override
    public MutualizationForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        // Document path
        String path = this.getDocumentPath(portalControllerContext);

        // Document
        Document document = this.repository.getDocument(portalControllerContext, path);
        // Document properties
        PropertyMap properties = document.getProperties();


        // Form
        MutualizationForm form = this.applicationContext.getBean(MutualizationForm.class);

        // Enable indicator
        boolean enable = BooleanUtils.isTrue(properties.getBoolean(MutualizationRepository.ENABLE_PROPERTY));
        form.setEnable(enable);
        
        DocumentDTO documentDto = this.documentDao.toDTO(document);

        // Title
        String title = document.getString(MutualizationRepository.TITLE_PROPERTY, documentDto.getDisplayTitle());
        form.setTitle(title);

        // Keywords
        Set<String> keywords = new HashSet<>(this.getPropertyList(properties, MutualizationRepository.KEYWORDS_PROPERTY));
        
        if( enable == false)    {
            // keyword suggestions (only on new mutualization)
            keywords.addAll(this.repository.getDocumentAncestors(portalControllerContext, path));
            PropertyList metaKeywords = document.getProperties().getList( "idxcl:keywords");
            if( metaKeywords != null)   {
                for(Object keyword:metaKeywords.list())
                    keywords.add((String) keyword);
            }
        }
       
        form.setSuggestedKeywords(keywords);
        form.setKeywords(keywords);

        // Document types
        List<String> documentTypes = this.getPropertyList(properties, MutualizationRepository.DOCUMENT_TYPES_PROPERTY);
        form.setDocumentTypes(documentTypes);

        // Levels
        List<String> levels = this.getPropertyList(properties, MutualizationRepository.LEVELS_PROPERTY);
        form.setLevels(levels);

        // Subjects
        List<String> subjects = this.getPropertyList(properties, MutualizationRepository.SUBJECTS_PROPERTY);
        form.setSubjects(subjects);
        
        // Licence
        String licence = document.getString(MutualizationRepository.LICENCE_PROPERTY, MutualizationRepository.LICENCE_DEFAULT_VALUE);
        form.setLicence(licence);
        
        // Subjects
        String comment = document.getString(MutualizationRepository.COMMENT_PROPERTY, "");
        form.setComment(StringEscapeUtils.unescapeHtml(comment.replaceAll("<br>", "\n")));

        return form;
    }


    /**
     * Get property list.
     *
     * @param properties document properties
     * @param name       property name
     * @return list
     */
    private List<String> getPropertyList(PropertyMap properties, String name) {
        PropertyList property = properties.getList(name);

        List<String> list;
        if ((property == null) || property.isEmpty()) {
            list = new ArrayList<>(0);
        } else {
            list = new ArrayList<>(property.size());

            for (int i = 0; i < property.size(); i++) {
                String value = property.getString(i);
                list.add(value);
            }
        }

        return list;
    }


    @Override
    public JSONArray loadVocabulary(PortalControllerContext portalControllerContext, String vocabulary, String filter) throws PortletException, IOException {
        // Vocabulary JSON array
        JSONArray array;
        if (vocabulary == null) {
            array = null;
        } else {
            array = this.repository.loadVocabulary(portalControllerContext, vocabulary);
        }

        // Select2 results
        JSONArray results;
        if ((array == null) || (array.isEmpty())) {
            results = new JSONArray();
        } else {
            results = this.parseVocabulary(array, filter);
        }

        return results;
    }


    /**
     * Parse vocabulary JSON array with filter.
     *
     * @param jsonArray JSON array
     * @param filter    filter, may be null
     * @return results
     */
    private JSONArray parseVocabulary(JSONArray jsonArray, String filter) throws IOException {
        Map<String, VocabularyItem> items = new HashMap<>(jsonArray.size());
        Set<String> rootItems = new LinkedHashSet<>();

        boolean multilevel = false;

        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;

            String key = jsonObject.getString("key");
            String value = jsonObject.getString("value");
            String parent = null;
            if (jsonObject.containsKey("parent")) {
                parent = jsonObject.getString("parent");
            }
            boolean matches = this.matchesVocabularyItem(value, filter);

            VocabularyItem item = items.get(key);
            if (item == null) {
                item = this.applicationContext.getBean(VocabularyItem.class, key);
                items.put(key, item);
            }
            item.setValue(value);
            item.setParent(parent);
            if (matches) {
                item.setMatches(true);
                item.setDisplayed(true);
            }

            if (StringUtils.isEmpty(parent)) {
                rootItems.add(key);
            } else {
                multilevel = true;

                VocabularyItem parentItem = items.get(parent);
                if (parentItem == null) {
                    parentItem = this.applicationContext.getBean(VocabularyItem.class, parent);
                    items.put(parent, parentItem);
                }
                parentItem.getChildren().add(key);

                if (item.isDisplayed()) {
                    while (parentItem != null) {
                        parentItem.setDisplayed(true);

                        if (StringUtils.isEmpty(parentItem.getParent())) {
                            parentItem = null;
                        } else {
                            parentItem = items.get(parentItem.getParent());
                        }
                    }
                }
            }
        }


        JSONArray results = new JSONArray();
        this.generateVocabularyChildren(items, results, rootItems, multilevel, 1, null);

        return results;
    }


    /**
     * Check if value matches filter.
     *
     * @param value  vocabulary item value
     * @param filter filter
     * @return true if value matches filter
     */
    private boolean matchesVocabularyItem(String value, String filter) throws UnsupportedEncodingException {
        boolean matches = true;

        if (filter != null) {
            // Decoded value
            String decodedValue = URLDecoder.decode(value, CharEncoding.UTF_8);
            // Diacritical value
            String diacriticalValue = Normalizer.normalize(decodedValue, Normalizer.Form.NFD).replaceAll("\\p{IsM}+", StringUtils.EMPTY);

            // Filter
            String[] splittedFilters = StringUtils.split(filter, "*");
            for (String splittedFilter : splittedFilters) {
                // Diacritical filter
                String diacriticalFilter = Normalizer.normalize(splittedFilter, Normalizer.Form.NFD).replaceAll("\\p{IsM}+", StringUtils.EMPTY);

                if (!StringUtils.containsIgnoreCase(diacriticalValue, diacriticalFilter)) {
                    matches = false;
                    break;
                }
            }
        }

        return matches;
    }


    /**
     * Generate vocabulary children.
     *
     * @param items     vocabulary items
     * @param jsonArray results JSON array
     * @param children  children
     * @param optgroup  options group presentation indicator
     * @param level     depth level
     * @param parentId  parent identifier
     */
    private void generateVocabularyChildren(Map<String, VocabularyItem> items, JSONArray jsonArray, Set<String> children, boolean optgroup, int level,
                                            String parentId) throws UnsupportedEncodingException {
        for (String child : children) {
            VocabularyItem item = items.get(child);
            if ((item != null) && item.isDisplayed()) {
                // Identifier
                String id;
                if (StringUtils.isEmpty(parentId)) {
                    id = item.getKey();
                } else {
                    id = parentId + "/" + item.getKey();
                }

                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("text", URLDecoder.decode(item.getValue(), CharEncoding.UTF_8));
                object.put("optgroup", optgroup);
                object.put("level", level);

                if (!item.isMatches()) {
                    object.put("disabled", true);
                }

                jsonArray.add(object);

                if (!item.getChildren().isEmpty()) {
                    this.generateVocabularyChildren(items, jsonArray, item.getChildren(), false, level + 1, id);
                }
            }
        }
    }


    @Override
    public void enable(PortalControllerContext portalControllerContext, MutualizationForm form) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Document path
        String documentPath = this.getDocumentPath(portalControllerContext);

        this.repository.enableMutualization(portalControllerContext, form, documentPath, MUTUALIZED_SPACE_PATH);
        
        
        // Notify CMS change
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        String spacePath = nuxeoController.getSpacePath(documentPath);
        
        //TODO : integrate publication to connect
        nuxeoController.notifyUpdate(documentPath, spacePath, UpdateScope.SCOPE_SPACE, false);
        //nuxeoController.notifyUpdate(documentPath, spacePath, UpdateScope.SCOPE_CONTENT, false);
        
        // Reset cache used for discussions
        DiscussionHelper.resetLocalPublications(portalControllerContext);

        // Notification
        String message;
        if (form.isEnable()) {
            message = bundle.getString("MUTUALIZATION_UPDATE_MESSAGE_SUCCESS");
        } else {
            message = bundle.getString("MUTUALIZATION_ENABLE_MESSAGE_SUCCESS");
        }
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);
    }


    @Override
    public void disable(PortalControllerContext portalControllerContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // Document path
        String documentPath = this.getDocumentPath(portalControllerContext);

        this.repository.disableMutualization(portalControllerContext, documentPath, MUTUALIZED_SPACE_PATH);
        
        // Notify CMS change
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
        String spacePath = nuxeoController.getSpacePath(documentPath);
        //TODO : integrate publication to connect
        nuxeoController.notifyUpdate(documentPath, spacePath, UpdateScope.SCOPE_SPACE, false);
        //nuxeoController.notifyUpdate(documentPath, spacePath, UpdateScope.SCOPE_CONTENT, false);
        
        // Reset cache used for discussions
        DiscussionHelper.resetLocalPublications(portalControllerContext);

        // Notification
        String message = bundle.getString("MUTUALIZATION_DISABLE_MESSAGE_SUCCESS");
        this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);
    }


    @Override
    public String getRedirectionUrl(PortalControllerContext portalControllerContext) {
        return this.portalUrlFactory.getBackURL(portalControllerContext, false, false);
    }


    /**
     * Get document path.
     *
     * @param portalControllerContext portal controller context
     * @return path
     */
    private String getDocumentPath(PortalControllerContext portalControllerContext) {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window
        PortalWindow window = WindowFactory.getWindow(request);

        return window.getProperty(DOCUMENT_PATH_WINDOW_PROPERTY);
    }

}
