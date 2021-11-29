package fr.index.cloud.ens.application.card;

import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Application edition form java-bean.
 *
 * @author Jean-Sébastien Steux
 */
@Component
@Refreshable
public class ApplicationCardForm {

    ApplicationCard bean;

    
    /**
     * Getter for bean.
     * @return the bean
     */
    public ApplicationCard getBean() {
        return bean;
    }

    
    /**
     * Setter for bean.
     * @param bean the bean to set
     */
    public void setBean(ApplicationCard bean) {
        this.bean = bean;
    }

    public ApplicationCardForm(ApplicationCard bean) {
        super();
        this.bean = bean;
    }


    public ApplicationCardForm() {
        super();

    }


}
