package fr.index.cloud.ens.directory.model.preferences;

import org.osivia.directory.v2.model.preferences.UserPreferencesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * User preferences customized implementation.
 *
 * @author Cédric Krommenhoek
 * @see UserPreferencesImpl
 * @see CustomizedUserPreferences
 */
@Component
@Primary
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CustomizedUserPreferencesImpl extends UserPreferencesImpl implements CustomizedUserPreferences {

    /**
     * File browser preferences.
     */
    private Map<String, CustomizedFileBrowserPreferences> fileBrowserPreferences;


    /**
     * Constructor.
     *
     * @param documentId document identifier
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public CustomizedUserPreferencesImpl(String documentId) {
        super(documentId);
    }


    @Override
    public Map<String, CustomizedFileBrowserPreferences> getFileBrowserPreferences() {
        return this.fileBrowserPreferences;
    }


    @Override
    public void setFileBrowserPreferences(Map<String, CustomizedFileBrowserPreferences> preferences) {
        this.fileBrowserPreferences = preferences;
    }

}
