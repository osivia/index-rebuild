package fr.index.cloud.ens.directory.person.card.portlet.model;

import org.osivia.services.person.card.portlet.model.PersonEditionForm;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Person edition customized form.
 *
 * @author Cédric Krommenhoek
 * @see PersonEditionForm
 */
@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomizedPersonEditionForm extends PersonEditionForm {

    /**
     * Nickname.
     */
    private String nickname;
    
    /** Uid. */
    private String uid;
    


    
    /**
     * Getter for uid.
     * @return the uid
     */
    public String getUid() {
        return uid;
    }


    
    /**
     * Setter for uid.
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }


    /**
     * Constructor.
     */
    public CustomizedPersonEditionForm() {
        super();
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
