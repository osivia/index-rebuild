package fr.index.cloud.ens.taskbar.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Service java-bean.
 *
 * @author Cédric Krommenhoek
 * @see Task
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceTask extends Task {

    public ServiceTask() {
        super();
    }

}
