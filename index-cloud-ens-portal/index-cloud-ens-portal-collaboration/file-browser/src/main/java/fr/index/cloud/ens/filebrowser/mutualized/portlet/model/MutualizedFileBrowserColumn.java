package fr.index.cloud.ens.filebrowser.mutualized.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserColumn;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Mutualized file browser column java-bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserColumn
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MutualizedFileBrowserColumn extends AbstractFileBrowserColumn {

    /**
     * Constructor.
     */
    public MutualizedFileBrowserColumn() {
        super();
    }

}
