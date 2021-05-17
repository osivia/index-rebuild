package fr.index.cloud.ens.filebrowser.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.CustomizedFileBrowserSortField;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * File browser customized portlet sort enumeration.
 *
 * @author Cédric Krommenhoek
 * @see CustomizedFileBrowserSortField
 */
public enum CustomizedFileBrowserSortEnum implements CustomizedFileBrowserSortField {

    /**
     * Relevance sort.
     */
    RELEVANCE(false, true),
    /**
     * Title sort.
     */
    TITLE,
    /**
     * Document type sort.
     */
    DOCUMENT_TYPE(true),
    /**
     * Level sort.
     */
    LEVEL(true),
    /**
     * Subject sort.
     */
    SUBJECT(true),
    /**
     * Last modification sort.
     */
    FORMAT(true),
    /**
     * Last modification sort.
     */
    LAST_MODIFICATION(true),
    /**
     * File size sort.
     */
    FILE_SIZE(true),
    /**
     * Location sort.
     */
    LOCATION(true, true);


    /**
     * Default configuration.
     */
    public static final List<CustomizedFileBrowserSortField> DEFAULT_CONFIGURATION = Arrays.asList(CustomizedFileBrowserSortEnum.DOCUMENT_TYPE, CustomizedFileBrowserSortEnum.LEVEL, CustomizedFileBrowserSortEnum.LAST_MODIFICATION, CustomizedFileBrowserSortEnum.FILE_SIZE, CustomizedFileBrowserSortEnum.LOCATION);


    /**
     * Identifier.
     */
    private final String id;
    /**
     * Internationalization key.
     */
    private final String key;
    /**
     * Configurable indicator.
     */
    private final boolean configurable;
    /**
     * List mode restriction indicator.
     */
    private final boolean listMode;


    /**
     * Constructor.
     *
     * @param configurable configurable indicator
     * @param listMode     list mode restriction indicator
     */
    CustomizedFileBrowserSortEnum(boolean configurable, boolean listMode) {
        this.id = StringUtils.lowerCase(StringUtils.replace(this.name(), "_", "-"));
        this.key = "FILE_BROWSER_SORT_FIELD_" + StringUtils.upperCase(this.name());
        this.configurable = configurable;
        this.listMode = listMode;
    }

    /**
     * Constructor.
     *
     * @param configurable configurable indicator
     */
    CustomizedFileBrowserSortEnum(boolean configurable) {
        this(configurable, false);
    }

    /**
     * Constructor.
     */
    CustomizedFileBrowserSortEnum() {
        this(false);
    }


    /**
     * Get sort from identifier
     *
     * @param id identifier
     * @return sort
     */
    public static CustomizedFileBrowserSortEnum fromId(String id) {
        CustomizedFileBrowserSortEnum result = null;
        for (CustomizedFileBrowserSortEnum value : CustomizedFileBrowserSortEnum.values()) {
            if (StringUtils.equals(id, value.id)) {
                result = value;
            }
        }
        return result;
    }


    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public String getKey() {
        return this.key;
    }


    @Override
    public boolean isListMode() {
        return this.listMode;
    }


    @Override
    public boolean isConfigurable() {
        return configurable;
    }

}
