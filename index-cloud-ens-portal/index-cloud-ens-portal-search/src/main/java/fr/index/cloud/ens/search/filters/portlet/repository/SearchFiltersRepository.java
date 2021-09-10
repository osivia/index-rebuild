package fr.index.cloud.ens.search.filters.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepository;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;

import java.util.List;

import javax.portlet.PortletException;

/**
 * Search filters portlet repository interface.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepository
 */
public interface SearchFiltersRepository extends SearchCommonRepository {

    /**
     * Search persons.
     *
     * @param portalControllerContext the portal controller context
     * @param filter the filter
     * @param tokenizer the tokenizer
     * @return the list
     * @throws PortletException the portlet exception
     */
    List<Person> searchPersons(PortalControllerContext portalControllerContext, String filter, boolean tokenizer) throws PortletException;

    /**
     * Search person by id.
     *
     * @param id the id
     * @return the person
     * @throws PortletException the portlet exception
     */
    Person searchPersonById( String id) throws PortletException;

}
