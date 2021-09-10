package fr.index.cloud.ens.search.filters.home.settings.portlet.model;

import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Search filters home settings form.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchFiltersHomeSettingsForm {

    /**
     * Mode.
     */
    private SearchFiltersHomeSettingsMode mode;
    /**
     * Levels.
     */
    private List<String> levels;
    /**
     * Subjects.
     */
    private List<String> subjects;
    /**
     * User saved search.
     */
    private UserSavedSearch savedSearch;


    /**
     * Constructor.
     */
    public SearchFiltersHomeSettingsForm() {
        super();
    }


    public SearchFiltersHomeSettingsMode getMode() {
        return mode;
    }

    public void setMode(SearchFiltersHomeSettingsMode mode) {
        this.mode = mode;
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

    public UserSavedSearch getSavedSearch() {
        return savedSearch;
    }

    public void setSavedSearch(UserSavedSearch savedSearch) {
        this.savedSearch = savedSearch;
    }
}
