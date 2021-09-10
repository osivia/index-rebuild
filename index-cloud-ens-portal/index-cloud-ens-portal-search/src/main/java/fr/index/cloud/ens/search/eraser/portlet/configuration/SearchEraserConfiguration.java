package fr.index.cloud.ens.search.eraser.portlet.configuration;

import fr.index.cloud.ens.search.common.portlet.configuration.SearchCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Search eraser portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.search.eraser.portlet")
public class SearchEraserConfiguration extends SearchCommonConfiguration {

    /**
     * Constructor.
     */
    public SearchEraserConfiguration() {
        super();
    }


    @Override
    protected String getJspFolder() {
        return "search-eraser";
    }

}
