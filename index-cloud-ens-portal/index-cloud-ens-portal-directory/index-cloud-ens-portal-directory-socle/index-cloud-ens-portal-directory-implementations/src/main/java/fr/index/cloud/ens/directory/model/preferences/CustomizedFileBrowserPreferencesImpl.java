package fr.index.cloud.ens.directory.model.preferences;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Customized file browser preferences implementation.
 *
 * @author Cédric Krommenhoek
 * @see CustomizedFileBrowserPreferences
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomizedFileBrowserPreferencesImpl implements CustomizedFileBrowserPreferences {

    /**
     * Identifier.
     */
    private String id;
    /**
     * Columns.
     */
    private List<CustomizedFileBrowserColumn> columns;


    /**
     * Constructor.
     */
    public CustomizedFileBrowserPreferencesImpl() {
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
    public List<CustomizedFileBrowserColumn> getColumns() {
        return this.columns;
    }


    @Override
    public void setColumns(List<CustomizedFileBrowserColumn> columns) {
        this.columns = columns;
    }

}
