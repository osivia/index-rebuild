package fr.index.cloud.ens.taskbar.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Taskbar window properties java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskbarWindowProperties {

    /**
     * Parent document path.
     */
    private String path;


    /**
     * Constructor.
     */
    public TaskbarWindowProperties() {
        super();
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
