package fr.index.cloud.ens.search.saved.portlet.configuration;

import fr.index.cloud.ens.search.common.portlet.configuration.SearchCommonConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Saved searches portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see SearchCommonConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.search.saved.portlet")
public class SavedSearchesConfiguration extends SearchCommonConfiguration {

    /**
     * Constructor.
     */
    public SavedSearchesConfiguration() {
        super();
    }


    @Override
    protected String getJspFolder() {
        return "saved-searches";
    }
    
    

}
