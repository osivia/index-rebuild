package fr.index.cloud.ens.search.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Search window properties.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchWindowProperties {

    /**
     * Search view.
     */
    private SearchView view;


    public SearchView getView() {
        return view;
    }

    public void setView(SearchView view) {
        this.view = view;
    }
}
