package fr.index.cloud.ens.search.filters.portlet.model.converter;

import fr.index.cloud.ens.search.filters.portlet.service.SearchFiltersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * Search filters date property editor.
 *
 * @author Cédric Krommenhoek
 * @see PropertyEditorSupport
 */
@Component
public class SearchFiltersDatePropertyEditor extends PropertyEditorSupport {

    /**
     * Portlet service.
     */
    @Autowired
    private SearchFiltersService service;


    /**
     * Constructor.
     */
    public SearchFiltersDatePropertyEditor() {
        super();
    }


    @Override
    public String getAsText() {
        String result;

        Object value = this.getValue();
        if (value instanceof Date) {
            Date date = (Date) value;
            result = this.service.formatDate(date);
        } else {
            result = super.getAsText();
        }

        return result;
    }


    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Date date = this.service.parseDate(text);
        this.setValue(date);
    }

}
