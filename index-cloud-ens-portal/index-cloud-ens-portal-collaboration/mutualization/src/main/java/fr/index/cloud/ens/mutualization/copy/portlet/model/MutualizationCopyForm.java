package fr.index.cloud.ens.mutualization.copy.portlet.model;

import org.osivia.portal.api.portlet.Refreshable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Mutualization copy form java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION)
public class MutualizationCopyForm {

    /**
     * Document path.
     */
    private String documentPath;
    /**
     * Base path.
     */
    private String basePath;

    /**
     * Target path.
     */
    private String targetPath;


    /**
     * Constructor.
     */
    public MutualizationCopyForm() {
        super();
    }


    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
}
