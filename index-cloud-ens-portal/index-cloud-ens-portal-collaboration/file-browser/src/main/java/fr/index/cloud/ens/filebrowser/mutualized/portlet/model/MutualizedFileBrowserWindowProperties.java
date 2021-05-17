package fr.index.cloud.ens.filebrowser.mutualized.portlet.model;

import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserWindowProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Mutualized file browser window properties java-bean.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserWindowProperties
 */
@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MutualizedFileBrowserWindowProperties extends FileBrowserWindowProperties {

    /**
     * Page size.
     */
    private Integer pageSize;


    /**
     * Constructor.
     */
    public MutualizedFileBrowserWindowProperties() {
        super();
    }


    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
