package fr.index.cloud.ens.search.filters.home.settings.portlet.configuration;

import fr.index.cloud.ens.search.common.portlet.configuration.SearchCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Search filters home settings portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonConfiguration
 */
@Configuration
@ComponentScan(basePackages = {"fr.index.cloud.ens.search.filters.home.settings.portlet", "fr.index.cloud.ens.search.common.portlet"})
public class SearchFiltersHomeSettingsConfiguration extends SearchCommonConfiguration {

    /**
     * Constructor.
     */
    public SearchFiltersHomeSettingsConfiguration() {
        super();
    }


    @Override
    protected String getJspFolder() {
        return "search-filters-home-settings";
    }

}
