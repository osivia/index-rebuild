package fr.index.cloud.ens.search.filters.location.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Search filters location form java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchFiltersLocationForm {

    /**
     * Target path.
     */
    private String targetPath;

    /**
     * Base path.
     */
    private String basePath;


    /**
     * Constructor.
     */
    public SearchFiltersLocationForm() {
        super();
    }


    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

}
