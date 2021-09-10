package fr.index.cloud.ens.search.saved.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Saved searches window properties.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SavedSearchesWindowProperties {

    /**
     * Saved searches category identifier.
     */
    private String categoryId;


    /**
     * Constructor.
     */
    public SavedSearchesWindowProperties() {
        super();
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
