package fr.index.cloud.ens.application.card;

import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Application card java-bean.
 *
 * @author Jean-Sébastien Steux
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ApplicationCard {


    /** Code. */
    private String code;

    /** Title. */
    private String title;
    /** Description. */
    private String description;


    /**
     * Constructor.
     */
    public ApplicationCard() {
        super();
    }


    /**
     * Getter for code.
     * 
     * @return the code
     */
    public String getCode() {
        return code;
    }


    /**
     * Setter for code.
     * 
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }


    /**
     * Getter for title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


}
