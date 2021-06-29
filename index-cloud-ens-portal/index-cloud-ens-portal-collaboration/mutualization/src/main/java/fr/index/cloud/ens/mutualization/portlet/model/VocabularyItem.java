package fr.index.cloud.ens.mutualization.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Vocabulary item java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VocabularyItem {

    /**
     * Vocabulary key.
     */
    private final String key;
    /**
     * Vocabulary children.
     */
    private final Set<String> children;

    /**
     * Vocabulary value.
     */
    private String value;
    /**
     * Vocabulary parent.
     */
    private String parent;
    /**
     * Displayed item indicator.
     */
    private boolean displayed;
    /**
     * Filter matches indicator.
     */
    private boolean matches;


    /**
     * Constructor.
     *
     * @param key vocabulary key
     */
    public VocabularyItem(String key) {
        super();
        this.key = key;
        this.children = new LinkedHashSet<>();
    }


    public String getKey() {
        return key;
    }

    public Set<String> getChildren() {
        return children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isMatches() {
        return matches;
    }

    public void setMatches(boolean matches) {
        this.matches = matches;
    }

}
