package fr.index.cloud.ens.search.filters.home.settings.portlet.model.converter;

import fr.index.cloud.ens.search.filters.home.settings.portlet.service.SearchFiltersHomeSettingsService;
import org.apache.commons.lang.math.NumberUtils;
import org.osivia.directory.v2.model.preferences.UserPreferences;
import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.osivia.directory.v2.service.preferences.UserPreferencesService;
import org.osivia.portal.api.PortalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * User saved search property editor.
 *
 * @author Cédric Krommenhoek
 * @see PropertyEditorSupport
 */
@Component
public class SavedSearchPropertyEditor extends PropertyEditorSupport {

    /**
     * Portlet service.
     */
    @Autowired
    private SearchFiltersHomeSettingsService service;

    /**
     * User preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;


    /**
     * Constructor.
     */
    public SavedSearchPropertyEditor() {
        super();
    }


    @Override
    public String getAsText() {
        Object value = this.getValue();

        String text;
        if (value instanceof UserSavedSearch) {
            UserSavedSearch savedSearch = (UserSavedSearch) value;
            text = String.valueOf(savedSearch.getId());
        } else {
            text = null;
        }

        return text;
    }


    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        int id = NumberUtils.toInt(text);

        if (id > 0) {
            UserSavedSearch savedSearch;
            try {
                savedSearch = this.userPreferencesService.createUserSavedSearch(null, id);
            } catch (PortalException e) {
                savedSearch = null;
            }
            this.setValue(savedSearch);
        }
    }

}
