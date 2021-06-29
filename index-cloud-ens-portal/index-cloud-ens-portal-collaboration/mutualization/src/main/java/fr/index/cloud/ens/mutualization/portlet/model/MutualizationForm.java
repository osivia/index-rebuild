package fr.index.cloud.ens.mutualization.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Mutualization form java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MutualizationForm {

    /**
     * Enable indicator.
     */
    private boolean enable;
    /**
     * Suggested keywords.
     */
    private Set<String> suggestedKeywords;

    /**
     * Title.
     */
    private String title;
    /**
     * Keywords.
     */
    private Set<String> keywords;
    /**
     * Document types.
     */
    private List<String> documentTypes;
    /**
     * Levels.
     */
    private List<String> levels;
    /**
     * Subjects.
     */
    private List<String> subjects;


    /**
     * Constructor.
     */
    public MutualizationForm() {
        super();
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Set<String> getSuggestedKeywords() {
        return suggestedKeywords;
    }

    public void setSuggestedKeywords(Set<String> suggestedKeywords) {
        this.suggestedKeywords = suggestedKeywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(List<String> documentTypes) {
        this.documentTypes = documentTypes;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

}
