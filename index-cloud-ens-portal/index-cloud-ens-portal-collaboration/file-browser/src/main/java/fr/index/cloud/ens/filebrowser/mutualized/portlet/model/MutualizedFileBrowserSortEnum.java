package fr.index.cloud.ens.filebrowser.mutualized.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.CustomizedFileBrowserSortField;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserSortEnum;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Mutualized file browser sorts enumeration.
 *
 * @author Cédric Krommenhoek
 * @see MutualizedFileBrowserSortField
 */
public enum MutualizedFileBrowserSortEnum implements MutualizedFileBrowserSortField {

    /**
     * Relevance sort.
     */
    RELEVANCE("relevance", false, null),
    /**
     * Title sort.
     */
    TITLE("title", false, "mtz:title"),
    /**
     * Document type sort.
     */
    DOCUMENT_TYPE("document-type", true, "idxcl:documentTypes"),
    /**
     * Level sort.
     */
    LEVEL("level", true, "idxcl:levels"),
    /**
     * Subject sort.
     */
    SUBJECT("subject", true, "idxcl:subjects"),
    /**
     * Subject sort.
     */
    FORMAT("format", true, "idxcl:formatText"),
    /**
     * Last modification sort.
     */
    LAST_MODIFICATION("last-modification", true, "dc:issued"),
    /**
     * Author sort.
     */
    AUTHOR("author", true, "dc:lastContributor"),
    /**
     * Author sort.
     */
    LICENCE("licence", true, "mtz:licence"),    
    /**
     * File size sort.
     */
    FILE_SIZE("file-size", true, "common:size"),
    /**
     * Views.
     */
    VIEWS("views", true, "mtz:liveviews"), 
    /**
     * Downloads.
     */
    DOWNLOADS("downloads", true, "mtz:livedownloads"); 


    /**
     * Default configuration.
     */
    public static final List<CustomizedFileBrowserSortField> DEFAULT_CONFIGURATION = Arrays.asList(MutualizedFileBrowserSortEnum.DOCUMENT_TYPE, MutualizedFileBrowserSortEnum.LAST_MODIFICATION, MutualizedFileBrowserSortEnum.AUTHOR, MutualizedFileBrowserSortEnum.VIEWS, MutualizedFileBrowserSortEnum.DOWNLOADS, MutualizedFileBrowserSortEnum.FILE_SIZE);


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
     * NXQL field.
     */
    private final String field;


    /**
     * Constructor.
     *
     * @param id           identifier
     * @param configurable configurable indicator
     * @param field        NXQL field
     */
    MutualizedFileBrowserSortEnum(String id, boolean configurable, String field) {
        this.id = id;
        this.key = "FILE_BROWSER_SORT_FIELD_" + StringUtils.upperCase(this.name());
        this.configurable = configurable;
        this.field = field;
    }


    /**
     * Get sort from identifier
     *
     * @param id identifier
     * @return sort
     */
    public static MutualizedFileBrowserSortEnum fromId(String id) {
        MutualizedFileBrowserSortEnum result = null;
        for (MutualizedFileBrowserSortEnum value : MutualizedFileBrowserSortEnum.values()) {
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
        return false;
    }


    @Override
    public boolean isConfigurable() {
        return this.configurable;
    }


    @Override
    public String getField() {
        return this.field;
    }

}
