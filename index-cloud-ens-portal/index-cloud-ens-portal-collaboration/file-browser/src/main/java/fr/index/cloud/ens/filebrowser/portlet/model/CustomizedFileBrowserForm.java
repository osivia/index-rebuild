package fr.index.cloud.ens.filebrowser.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserForm;
import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * File browser customized form java-bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserForm
 */
@Component
@Primary
@Scope(WebApplicationContext.SCOPE_SESSION)
@Refreshable
public class CustomizedFileBrowserForm extends AbstractFileBrowserForm {

    /** Title. */
    private String title;


    /**
     * Constructor.
     */
    public CustomizedFileBrowserForm() {
        super();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
