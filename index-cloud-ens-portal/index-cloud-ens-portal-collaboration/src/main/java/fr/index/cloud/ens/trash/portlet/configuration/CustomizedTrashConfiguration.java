package fr.index.cloud.ens.trash.portlet.configuration;

import org.osivia.services.workspace.portlet.configuration.TrashConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Trash customized portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see TrashConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.trash.portlet")
public class CustomizedTrashConfiguration extends TrashConfiguration {

    /**
     * Constructor.
     */
    public CustomizedTrashConfiguration() {
        super();
    }

}
