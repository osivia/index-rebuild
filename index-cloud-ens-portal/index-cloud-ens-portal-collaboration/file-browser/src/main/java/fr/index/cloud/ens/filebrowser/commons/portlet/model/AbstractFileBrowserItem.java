package fr.index.cloud.ens.filebrowser.commons.portlet.model;

import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserItem;

import java.util.List;

/**
 * File browser item java-bean abstract super-class.
 *
 * @author Cédric Krommenhoek
 */
public abstract class AbstractFileBrowserItem extends FileBrowserItem {

    /**
     * Document types.
     */
    private List<String> documentTypes;
    /**
     * Levels.
     */
    private List<String> levels;
    /**
     * Subjects.
     */
    private List<String> subjects;
    
    /**
     *  Format.
     */
    private String format;




    /**
     * Constructor.
     */
    public AbstractFileBrowserItem() {
        super();
    }


    public List<String> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(List<String> documentTypes) {
        this.documentTypes = documentTypes;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
    

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
