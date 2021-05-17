package fr.index.cloud.ens.directory.model.preferences;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Customized file browser column implementation.
 *
 * @author Cédric Krommenhoek
 * @see CustomizedFileBrowserColumn
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomizedFileBrowserColumnImpl implements CustomizedFileBrowserColumn {

    /**
     * Identifier.
     */
    private String id;
    /**
     * Order.
     */
    private int order;
    /**
     * Visible indicator.
     */
    private boolean visible;


    /**
     * Constructor.
     */
    public CustomizedFileBrowserColumnImpl() {
        super();
    }


    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
