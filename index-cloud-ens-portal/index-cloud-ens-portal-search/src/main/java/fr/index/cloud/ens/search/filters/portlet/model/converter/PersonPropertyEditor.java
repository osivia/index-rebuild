package fr.index.cloud.ens.search.filters.portlet.model.converter;

import java.beans.PropertyEditorSupport;

import javax.portlet.PortletException;

import org.osivia.directory.v2.model.CollabProfile;
import org.osivia.directory.v2.service.WorkspaceService;
import org.osivia.portal.api.directory.v2.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.index.cloud.ens.search.filters.portlet.model.CustomPerson;
import fr.index.cloud.ens.search.filters.portlet.repository.SearchFiltersRepository;

/**
 * Person property editor.
 * 
 * @author Jean-Sébastien SSteux
 * @see PropertyEditorSupport
 */
@Component
public class PersonPropertyEditor extends PropertyEditorSupport {


    @Autowired SearchFiltersRepository repository;

    /**
     * Constructor.
     */
    public PersonPropertyEditor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsText() {
        Object value = this.getValue();

        String text;

        if ((value != null) && (value instanceof CustomPerson)) {
            text = ((CustomPerson) value).getId();
        } else {
            text = null;
        }

        return text;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // Local group
        Person person;
        try {
            person = this.repository.searchPersonById(text);
        } catch (PortletException e) {
            throw new IllegalArgumentException();
        }

        this.setValue(new CustomPerson(person));
    }

}
