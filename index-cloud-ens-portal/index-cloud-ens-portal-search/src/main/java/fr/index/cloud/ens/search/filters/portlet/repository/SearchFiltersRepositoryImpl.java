package fr.index.cloud.ens.search.filters.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepositoryImpl;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;

import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Search filters portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepositoryImpl
 * @see SearchFiltersRepository
 */
@Repository
public class SearchFiltersRepositoryImpl extends SearchCommonRepositoryImpl implements SearchFiltersRepository {

    /**
     * Constructor.
     */
    public SearchFiltersRepositoryImpl() {
        super();
    }


    /** Person service. */
    @Autowired
    private PersonUpdateService personService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> searchPersons(PortalControllerContext portalControllerContext, String filter, boolean tokenizer) throws PortletException {
        // Criteria
        Person criteria = this.personService.getEmptyPerson();

        if (tokenizer) {
            criteria.setMail(filter);
        } else {
            String tokenizedFilterSubStr = "*" + filter + "*";
            criteria.setDisplayName(tokenizedFilterSubStr);
        }
        
        List<Person> persons = this.personService.findByCriteria(criteria);
        List<Person> filteredPersons = new ArrayList<Person>();
        
        for( Person person: persons)    {
            if(person.getValidity() == null)    {
                filteredPersons.add(person);
            }
        }

        return filteredPersons;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Person searchPersonById( String id) throws PortletException {
        // Criteria
        Person criteria = this.personService.getEmptyPerson();

        criteria.setUid(id);
        
        List<Person> persons = this.personService.findByCriteria(criteria);
         
        for( Person person: persons)    {
            if(person.getValidity() == null)    {
                return person;
            }
        }

        return null;
    }
    
}
