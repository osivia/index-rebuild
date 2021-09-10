package fr.index.cloud.ens.search.common.portlet.model;

import org.apache.commons.lang.StringUtils;

/**
 * Search filters vocabularies enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum SearchFiltersVocabulary {

    /**
     * Levels.
     */
    LEVELS("idx_level", "SEARCH_FILTERS_LEVEL_ALL"),
    /**
     * Subjects.
     */
    SUBJECTS("idx_subject", "SEARCH_FILTERS_SUBJECT_ALL"),
    /**
     * Document types.
     */
    DOCUMENT_TYPES("idx_document_type", "SEARCH_FILTERS_DOCUMENT_TYPE_ALL"),

    /**
     * Document types.
     */
    FILE_FORMATS("idx_file_format", "SEARCH_FILTERS_FILE_FORMAT_ALL"),
    /**
     * Share.
     */
    SHARED("idx_shared", "SEARCH_FILTERS_SHARED_ALL");
    
    /**
     * Vocabulary name.
     */
    private final String vocabularyName;
    /**
     * "All" internationalization key.
     */
    private final String allKey;


    /**
     * Constructor.
     *
     * @param vocabularyName vocabulary name
     * @param "All"          internationalization key
     */
    SearchFiltersVocabulary(String vocabularyName, String allKey) {
        this.vocabularyName = vocabularyName;
        this.allKey = allKey;
    }


    /**
     * Get search filters vocabulary from vocabulary name.
     *
     * @param vocabularyName vocabulary name
     * @return search filters vocabulary
     */
    public static final SearchFiltersVocabulary fromVocabularyName(String vocabularyName) {
        SearchFiltersVocabulary result = null;
        for (SearchFiltersVocabulary value : SearchFiltersVocabulary.values()) {
            if (StringUtils.equals(vocabularyName, value.vocabularyName)) {
                result = value;
            }
        }
        return result;
    }


    public String getVocabularyName() {
        return vocabularyName;
    }

    public String getAllKey() {
        return allKey;
    }
}
