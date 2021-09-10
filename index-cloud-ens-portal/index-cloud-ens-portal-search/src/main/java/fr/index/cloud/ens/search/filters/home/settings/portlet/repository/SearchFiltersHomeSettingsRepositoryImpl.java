package fr.index.cloud.ens.search.filters.home.settings.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepositoryImpl;
import org.springframework.stereotype.Repository;

/**
 * Search filters home settings portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepositoryImpl
 * @see SearchFiltersHomeSettingsRepository
 */
@Repository
public class SearchFiltersHomeSettingsRepositoryImpl extends SearchCommonRepositoryImpl implements SearchFiltersHomeSettingsRepository {

    /**
     * Constructor.
     */
    public SearchFiltersHomeSettingsRepositoryImpl() {
        super();
    }

}
