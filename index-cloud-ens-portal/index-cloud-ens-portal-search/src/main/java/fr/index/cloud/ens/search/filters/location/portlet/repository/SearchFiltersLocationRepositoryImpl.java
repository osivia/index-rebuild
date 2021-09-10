package fr.index.cloud.ens.search.filters.location.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepositoryImpl;
import org.springframework.stereotype.Repository;

/**
 * Search filters location portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepositoryImpl
 * @see SearchFiltersLocationRepository
 */
@Repository
public class SearchFiltersLocationRepositoryImpl extends SearchCommonRepositoryImpl implements SearchFiltersLocationRepository {

    /**
     * Constructor.
     */
    public SearchFiltersLocationRepositoryImpl() {
        super();
    }

}
