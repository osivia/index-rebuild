package fr.index.cloud.ens.filebrowser.portlet.service;

import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserColumn;
import fr.index.cloud.ens.directory.model.preferences.CustomizedFileBrowserPreferences;
import fr.index.cloud.ens.directory.model.preferences.CustomizedUserPreferences;
import fr.index.cloud.ens.directory.service.preferences.CustomizedUserPreferencesService;
import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserColumn;
import fr.index.cloud.ens.filebrowser.commons.portlet.model.CustomizedFileBrowserSortField;
import fr.index.cloud.ens.filebrowser.commons.portlet.service.AbstractFileBrowserServiceImpl;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserForm;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserItem;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserSortEnum;
import fr.toutatice.portail.cms.nuxeo.api.PageSelectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortField;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserWindowProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import java.util.*;

/**
 * File browser customized portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserServiceImpl
 * @see CustomizedFileBrowserService
 */
@Service
@Primary
public class CustomizedFileBrowserServiceImpl extends AbstractFileBrowserServiceImpl implements CustomizedFileBrowserService {

    /**
     * Selector identifiers.
     */
    public static final List<String> SELECTOR_IDENTIFIERS = Arrays.asList(KEYWORDS_SELECTOR_ID, DOCUMENT_TYPES_SELECTOR_ID, LEVELS_SELECTOR_ID, SUBJECTS_SELECTOR_ID, COMPUTED_SIZE_SELECTOR_ID, COMPUTED_DATE_SELECTOR_ID, FORMATS_SELECTOR_ID, SHAREDS_SELECTOR_ID);


    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;


    /**
     * Constructor.
     */
    public CustomizedFileBrowserServiceImpl() {
        super();
    }


    @Override
    public CustomizedFileBrowserForm getForm(PortalControllerContext portalControllerContext) throws PortletException {
        return (CustomizedFileBrowserForm) super.getForm(portalControllerContext);
    }


    @Override
    protected List<AbstractFileBrowserColumn> getFileBrowserColumns(PortalControllerContext portalControllerContext, CustomizedUserPreferences userPreferences) {
        CustomizedFileBrowserPreferences fileBrowserPreferences = this.getFileBrowserPreferences(userPreferences);

        // Columns
        List<AbstractFileBrowserColumn> columns;
        if (CollectionUtils.isEmpty(fileBrowserPreferences.getColumns())) {
            columns = null;
        } else {
            columns = new ArrayList<>();
            for (CustomizedFileBrowserColumn fileBrowserColumn : fileBrowserPreferences.getColumns()) {
                CustomizedFileBrowserSortEnum type = CustomizedFileBrowserSortEnum.fromId(fileBrowserColumn.getId());

                if ((type != null) && type.isConfigurable() && fileBrowserColumn.isVisible()) {
                    AbstractFileBrowserColumn column = this.applicationContext.getBean(AbstractFileBrowserColumn.class);
                    column.setId(type.getId());
                    column.setOrder(fileBrowserColumn.getOrder());

                    columns.add(column);
                }
            }
            Collections.sort(columns);
        }

        return columns;
    }


    @Override
    protected List<String> getSupportedSelectors() {
        return SELECTOR_IDENTIFIERS;
    }


    @Override
    protected boolean isListMode(PortalControllerContext portalControllerContext) {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();
        // Window properties
        FileBrowserWindowProperties windowProperties = this.getWindowProperties(portalControllerContext);
        // Selectors
        Map<String, List<String>> selectors = PageSelectors.decodeProperties(request.getParameter(SELECTORS_PARAMETER));

        boolean listMode = BooleanUtils.isTrue(windowProperties.getListMode());
        if (!listMode && MapUtils.isNotEmpty(selectors)) {
            Iterator<String> iterator = SELECTOR_IDENTIFIERS.iterator();
            while (!listMode && iterator.hasNext()) {
                String id = iterator.next();
                listMode = CollectionUtils.isNotEmpty(selectors.get(id));
            }
        }

        return listMode;
    }


    @Override
    protected CustomizedFileBrowserItem createItem(PortalControllerContext portalControllerContext, Document nuxeoDocument) {
        CustomizedFileBrowserItem item = (CustomizedFileBrowserItem) super.createItem(portalControllerContext, nuxeoDocument);

        // PRONOTE indicator
        PropertyList targets = nuxeoDocument.getProperties().getList("rshr:targets");
        boolean pronote = (targets != null) && !targets.isEmpty();
        item.setPronote(pronote);
        
        // SHARED indicator
        boolean shared = BooleanUtils.isTrue(nuxeoDocument.getProperties().getBoolean("rshr:enabledLink"));
        item.setShared(shared);

        // Mutualized indicator
        boolean mutualized = BooleanUtils.isTrue(nuxeoDocument.getProperties().getBoolean("mtz:enable"));
        item.setMutualized(mutualized);
        super.createItem(portalControllerContext, nuxeoDocument);

        return item;
    }


    @Override
    protected String getFileBrowserId() {
        return FILE_BROWSER_ID;
    }


    @Override
    public List<FileBrowserSortField> getSortFields(PortalControllerContext portalControllerContext) throws PortletException {
        CustomizedFileBrowserSortEnum[] values = CustomizedFileBrowserSortEnum.values();

        // Form
        CustomizedFileBrowserForm form = this.getForm(portalControllerContext);

        // Default sort field
        FileBrowserSortField defaultSortField = this.getDefaultSortField(portalControllerContext);

        // Filtered sort fields
        List<FileBrowserSortField> filteredFields = new ArrayList<>();
        for (CustomizedFileBrowserSortEnum value : values) {
            if ((form.isListMode() || !value.isListMode()) && !(StringUtils.equals(CustomizedFileBrowserSortEnum.RELEVANCE.getId(), value.getId()) && ((defaultSortField == null) || !StringUtils.equals(CustomizedFileBrowserSortEnum.RELEVANCE.getId(), defaultSortField.getId())))) {
                filteredFields.add(value);
            }
        }

        return filteredFields;
    }


    @Override
    protected List<FileBrowserSortField> getAllSortFields() {
        // Enum values
        CustomizedFileBrowserSortEnum[] values = CustomizedFileBrowserSortEnum.values();

        return new ArrayList<FileBrowserSortField>(Arrays.asList(values));
    }


    @Override
    protected FileBrowserSortField getDefaultSortField(PortalControllerContext portalControllerContext) {
        // Window properties
        FileBrowserWindowProperties windowProperties = this.getWindowProperties(portalControllerContext);

        // Default sort field
        FileBrowserSortField field;
        if (windowProperties.getDefaultSortField() != null) {
            field = this.getSortField(portalControllerContext, windowProperties.getDefaultSortField(), false);
        } else if (this.isListMode(portalControllerContext)) {
            field = CustomizedFileBrowserSortEnum.RELEVANCE;
        } else {
            field = CustomizedFileBrowserSortEnum.TITLE;
        }

        return field;
    }


    @Override
    protected void addToolbarItem(Element toolbar, String url, String target, String title, String icon, boolean noAjaxLink) {
        // Base HTML classes
        String baseHtmlClasses = "btn btn-link btn-link-hover-green text-green-dark btn-sm mr-1";
        
        if(noAjaxLink)
            baseHtmlClasses += " no-ajax-link";

        // Item
        Element item;
        if (StringUtils.isEmpty(url)) {
            item = DOM4JUtils.generateLinkElement("#", null, null, baseHtmlClasses + " disabled", null, icon);
        } else {
            // Data attributes
            Map<String, String> data = new HashMap<>();

            if ("#osivia-modal".equals(target)) {
                data.put("target", "#osivia-modal");
                data.put("load-url", url);
                data.put("title", title);

                url = "javascript:";
                target = null;
            } else if ("modal".equals(target)) {
                data.put("toggle", "modal");

                target = null;
            }

            item = DOM4JUtils.generateLinkElement(url, target, null, baseHtmlClasses , null, icon);

            // Title
            DOM4JUtils.addAttribute(item, "title", title);

            // Data attributes
            for (Map.Entry<String, String> entry : data.entrySet()) {
                DOM4JUtils.addDataAttribute(item, entry.getKey(), entry.getValue());
            }
        }

        // Text
        Element text = DOM4JUtils.generateElement("span", "d-none d-lg-inline", title);
        item.add(text);

        toolbar.add(item);
    }

}
