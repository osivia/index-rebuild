package fr.index.cloud.ens.taskbar.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Filters title task.
 *
 * @author Cédric Krommenhoek
 * @see Task
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FiltersTask extends Task {

    // Saved searches
    private List<Task> savedSearches;


    /**
     * Constructor.
     */
    public FiltersTask() {
        super();
    }


    @Override
    public boolean isFilters() {
        return true;
    }


    public List<Task> getSavedSearches() {
        return savedSearches;
    }

    public void setSavedSearches(List<Task> savedSearches) {
        this.savedSearches = savedSearches;
    }
}
