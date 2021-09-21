package fr.index.cloud.ens.directory.person.card.portlet.configuration;

import org.osivia.services.person.card.portlet.configuration.PersonCardConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Person card customized portlet configuration.
 *
 * @author Cédric Krommenhoek
 * @see PersonCardConfiguration
 */
@Configuration
@ComponentScan(basePackages = "fr.index.cloud.ens.directory.person.card.portlet")
public class CustomizedPersonCardConfiguration extends PersonCardConfiguration {

    /**
     * Constructor.
     */
    public CustomizedPersonCardConfiguration() {
        super();
    }


    @Override
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource messageSource = super.getMessageSource();
        messageSource.setBasenames("Resource", "customized-person-card");
        return messageSource;
    }

}
