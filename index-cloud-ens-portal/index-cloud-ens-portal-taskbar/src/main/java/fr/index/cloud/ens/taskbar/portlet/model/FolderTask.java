package fr.index.cloud.ens.taskbar.portlet.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.SortedSet;

/**
 * Folder java-bean.
 *
 * @author Cédric Krommenhoek
 * @see Task
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderTask extends Task {

    /**
     * Children.
     */
    private SortedSet<FolderTask> children;

    /**
     * Identifier.
     */
    private String id;
    /**
     * Path.
     */
    private String path;
    /**
     * Selected indicator.
     */
    private boolean selected;
    /**
     * Folder indicator.
     */
    private boolean folder;
    /**
     * Lazy indicator.
     */
    private boolean lazy;
    /**
     * Accepted types.
     */
    private String[] acceptedTypes;


    /**
     * Constructor.
     */
    public FolderTask() {
        super();
    }


    @Override
    public boolean isFancytree() {
        return true;
    }


    /**
     * Getter for children.
     *
     * @return the children
     */
    public SortedSet<FolderTask> getChildren() {
        return children;
    }

    /**
     * Setter for children.
     *
     * @param children the children to set
     */
    public void setChildren(SortedSet<FolderTask> children) {
        this.children = children;
    }

    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Setter for path.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Getter for selected.
     *
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter for selected.
     *
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Getter for folder.
     *
     * @return the folder
     */
    public boolean isFolder() {
        return folder;
    }

    /**
     * Setter for folder.
     *
     * @param folder the folder to set
     */
    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    /**
     * Getter for lazy.
     *
     * @return the lazy
     */
    public boolean isLazy() {
        return lazy;
    }

    /**
     * Setter for lazy.
     *
     * @param lazy the lazy to set
     */
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    /**
     * Getter for acceptedTypes.
     *
     * @return the acceptedTypes
     */
    public String[] getAcceptedTypes() {
        return acceptedTypes;
    }

    /**
     * Setter for acceptedTypes.
     *
     * @param acceptedTypes the acceptedTypes to set
     */
    public void setAcceptedTypes(String[] acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }
}
