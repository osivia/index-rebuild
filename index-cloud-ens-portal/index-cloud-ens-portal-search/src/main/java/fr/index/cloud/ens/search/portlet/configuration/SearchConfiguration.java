package fr.index.cloud.ens.search.portlet.configuration;

import fr.index.cloud.ens.search.common.portlet.configuration.SearchCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Search portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.search.portlet")
public class SearchConfiguration extends SearchCommonConfiguration {

    /**
     * Constructor.
     */
    public SearchConfiguration() {
        super();
    }


    @Override
    protected String getJspFolder() {
        return "search";
    }

}
