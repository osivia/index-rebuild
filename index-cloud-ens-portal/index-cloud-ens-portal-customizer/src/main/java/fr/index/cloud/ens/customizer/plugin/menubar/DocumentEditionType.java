package fr.index.cloud.ens.customizer.plugin.menubar;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Document edition types enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum DocumentEditionType {

    /**
     * Folder.
     */
    FOLDER("Folder", true, false),
    /**
     * File.
     */
    FILE("File"),
    /**
     * Picture file.
     */
    PICTURE("Picture", false, true),
    /**
     * Audio file.
     */
    AUDIO("Audio", false, true),
    /**
     * Video file.
     */
    VIDEO("Video", false, true);


    /**
     * Names cache map.
     */
    private static Map<String, DocumentEditionType> names;
    /**
     * Menubar "add" identifiers cache map.
     */
    private static Map<String, DocumentEditionType> addIds;

    /**
     * Type name.
     */
    private final String name;
    /**
     * Menubar "add" identifier.
     */
    private final String addId;
    /**
     * Allow type creation indicator.
     */
    private final boolean creation;
    /**
     * Allow type edition indicator.
     */
    private final boolean edition;


    /**
     * Constructor.
     *
     * @param name     type name
     * @param creation allow type creation indicator
     * @param edition  allow type edition indicator
     */
    DocumentEditionType(String name, boolean creation, boolean edition) {
        this.name = name;
        this.addId = "ADD_" + StringUtils.upperCase(name);
        this.creation = creation;
        this.edition = edition;
    }

    /**
     * Constructor.
     *
     * @param name type name
     */
    DocumentEditionType(String name) {
        this(name, true, true);
    }


    /**
     * Get document edition type from name.
     *
     * @param name type name
     * @return document edition type
     */
    public static DocumentEditionType fromName(String name) {
        if (names == null) {
            initNames();
        }

        return names.get(name);
    }


    /**
     * Names cache map initialization.
     */
    private synchronized static void initNames() {
        if (names == null) {
            DocumentEditionType[] values = DocumentEditionType.values();
            names = new ConcurrentHashMap<>(values.length);
            for (DocumentEditionType value : values) {
                names.put(value.name, value);
            }
        }
    }


    /**
     * Get document edition type from menubar "add" identifier.
     *
     * @param addId menubar "add" identifier
     * @return document edition type
     */
    public static DocumentEditionType fromAddId(String addId) {
        if (addIds == null) {
            initAddIds();
        }

        return addIds.get(addId);
    }


    /**
     * Menubar "add" identifiers cache map initialization.
     */
    private synchronized static void initAddIds() {
        if (addIds == null) {
            DocumentEditionType[] values = DocumentEditionType.values();
            addIds = new ConcurrentHashMap<>(values.length);
            for (DocumentEditionType value : values) {
                addIds.put(value.addId, value);
            }
        }
    }


    public String getName() {
        return name;
    }

    public String getAddId() {
        return addId;
    }

    public boolean isCreation() {
        return creation;
    }

    public boolean isEdition() {
        return edition;
    }

}
