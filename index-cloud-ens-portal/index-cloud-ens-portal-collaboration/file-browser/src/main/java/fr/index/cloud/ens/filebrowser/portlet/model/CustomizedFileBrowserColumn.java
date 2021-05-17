package fr.index.cloud.ens.filebrowser.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserColumn;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Customized file browser column java-bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserColumn
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomizedFileBrowserColumn extends AbstractFileBrowserColumn {

    /**
     * Constructor.
     */
    public CustomizedFileBrowserColumn() {
        super();
    }

}
