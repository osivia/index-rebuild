package fr.index.cloud.ens.taskbar.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Add task.
 *
 * @author Cédric Krommenhoek
 * @see Task
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddTask extends Task {

    /**
     * Dropdown items.
     */
    private List<AddDropdownItem> dropdownItems;


    @Override
    public boolean isAdd() {
        return true;
    }


    public List<AddDropdownItem> getDropdownItems() {
        return dropdownItems;
    }

    public void setDropdownItems(List<AddDropdownItem> dropdownItems) {
        this.dropdownItems = dropdownItems;
    }
}
