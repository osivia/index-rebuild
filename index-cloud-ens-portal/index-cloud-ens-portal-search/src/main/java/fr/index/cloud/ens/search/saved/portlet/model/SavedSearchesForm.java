package fr.index.cloud.ens.search.saved.portlet.model;

import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Saved searches form java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SavedSearchesForm {

    /**
     * User saved searches.
     */
    private List<UserSavedSearch> savedSearches;


    /**
     * Constructor.
     */
    public SavedSearchesForm() {
        super();
    }


    public List<UserSavedSearch> getSavedSearches() {
        return savedSearches;
    }

    public void setSavedSearches(List<UserSavedSearch> savedSearches) {
        this.savedSearches = savedSearches;
    }
}
