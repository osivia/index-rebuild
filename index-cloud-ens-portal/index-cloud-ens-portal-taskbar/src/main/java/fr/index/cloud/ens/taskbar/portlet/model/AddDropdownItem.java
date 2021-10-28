package fr.index.cloud.ens.taskbar.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * "add" dropdown item java-bean.
 *
 * @author Cédric Krommenhoek
 * @see Task
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddDropdownItem extends Task {

    /**
     * Customized icon.
     */
    private String customizedIcon;
    /**
     * Modal title.
     */
    private String modalTitle;


    /**
     * Constructor.
     */
    public AddDropdownItem() {
        super();
    }


    public String getCustomizedIcon() {
        return customizedIcon;
    }

    public void setCustomizedIcon(String customizedIcon) {
        this.customizedIcon = customizedIcon;
    }

    public String getModalTitle() {
        return modalTitle;
    }

    public void setModalTitle(String modalTitle) {
        this.modalTitle = modalTitle;
    }

}
