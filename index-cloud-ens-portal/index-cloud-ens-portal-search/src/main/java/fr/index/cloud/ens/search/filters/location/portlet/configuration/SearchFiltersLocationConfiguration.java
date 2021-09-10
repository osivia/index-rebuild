package fr.index.cloud.ens.search.filters.location.portlet.configuration;

import fr.index.cloud.ens.search.common.portlet.configuration.SearchCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Search filters location portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.search.filters.location.portlet")
public class SearchFiltersLocationConfiguration extends SearchCommonConfiguration {

    /**
     * Constructor.
     */
    public SearchFiltersLocationConfiguration() {
        super();
    }


    @Override
    protected String getJspFolder() {
        return "search-filters-location";
    }

}
