package fr.index.cloud.ens.mutualization.copy.portlet.model;

import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Mutualization copy confirmation form java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MutualizationCopyConfirmationForm {

    /**
     * Location.
     */
    private DocumentDTO location;

    /**
     * Choice.
     */
    private String choice;


    /**
     * Constructor.
     */
    public MutualizationCopyConfirmationForm() {
        super();
    }


    public DocumentDTO getLocation() {
        return location;
    }

    public void setLocation(DocumentDTO location) {
        this.location = location;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
