package fr.index.cloud.ens.filebrowser.mutualized.portlet.model;

import fr.index.cloud.ens.filebrowser.commons.portlet.model.AbstractFileBrowserForm;
import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Mutualized file browser form java-bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserForm
 */
@Component
@Primary
@Scope(WebApplicationContext.SCOPE_SESSION)
@Refreshable
public class MutualizedFileBrowserForm extends AbstractFileBrowserForm {

    /**
     * Total.
     */
    private int total;
    /**
     * Next page index.
     */
    private int nextPageIndex;


    /**
     * Constructor.
     */
    public MutualizedFileBrowserForm() {
        super();
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNextPageIndex() {
        return nextPageIndex;
    }

    public void setNextPageIndex(int nextPageIndex) {
        this.nextPageIndex = nextPageIndex;
    }
}
