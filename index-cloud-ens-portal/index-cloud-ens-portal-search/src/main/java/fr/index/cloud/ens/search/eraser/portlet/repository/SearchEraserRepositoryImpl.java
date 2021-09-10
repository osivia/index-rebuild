package fr.index.cloud.ens.search.eraser.portlet.repository;

import fr.index.cloud.ens.search.common.portlet.repository.SearchCommonRepositoryImpl;
import org.springframework.stereotype.Repository;

/**
 * Search eraser portlet repository implementation.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonRepositoryImpl
 * @see SearchEraserRepository
 */
@Repository
public class SearchEraserRepositoryImpl extends SearchCommonRepositoryImpl implements SearchEraserRepository {

    /**
     * Constructor.
     */
    public SearchEraserRepositoryImpl() {
        super();
    }

}
