package fr.index.cloud.ens.elasticsearch;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.ecm.es.customizer.writers.api.AbstractCustomJsonESWriter;
import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;

/**
 * Customized JSON Elasticsearch document writer.
 * 
 * @author Cédric Krommenhoek
 * @see AbstractCustomJsonESWriter
 */
public class CustomizedJsonESDocumentWriter extends AbstractCustomJsonESWriter {

    /** Schema. */
    private static final String SCHEMA = "indexClassifiers";

    /** Levels xpath. */
    private static final String LEVELS_XPATH = "idxcl:levels";
    /** Levels denormalized field. */
    private static final String LEVELS_DENORMALIZED_FIELD = "idxcl:levelsTree";
    /** Levels vocabulary name. */
    private static final String LEVELS_VOCABULARY_NAME = "idx_level";

    /** Subjects xpath. */
    private static final String SUBJECTS_XPATH = "idxcl:subjects";
    /** Subjects denormalized field. */
    private static final String SUBJECTS_DENORMALIZED_FIELD = "idxcl:subjectsTree";
    /** Subjects vocabulary name. */
    private static final String SUBJECTS_VOCABULARY_NAME = "idx_subject";
    
    

    /** File format denormalized field. */
    private static final String FILE_FORMAT_DENORMALIZED_FIELD = "idxcl:format";
    /** File format  vocabulary name. */
    private static final String FILE_FORMAT_VOCABULARY_NAME = "idx_file_format";

    /** File format  vocabulary name. */
    private static final String MIME_TYPE_VOCABULARY_NAME = "idx_mime_type_format";
    
     /** The file label. */
    private static final String FILE_LABEL = "Fichier";
    
    /** Vocabularies. */
    private Map<String, Map<String, VocabularyEntry>> vocabularies;


    /** Log. */
    private final Log log;
    
    
   

    /**
     * Constructor.
     */
    public CustomizedJsonESDocumentWriter() {
        super();

        // Log
        this.log = LogFactory.getLog(this.getClass());
        
        
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(DocumentModel document) {
        return document.hasSchema(SCHEMA);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void writeData(JsonGenerator jsonGenerator, DocumentModel document, String[] schemas, Map<String, String> contextParameters) throws IOException {
        // Levels
        this.writeTree(jsonGenerator, document, LEVELS_XPATH, LEVELS_DENORMALIZED_FIELD, LEVELS_VOCABULARY_NAME);

        // Subjects
        this.writeTree(jsonGenerator, document, SUBJECTS_XPATH, SUBJECTS_DENORMALIZED_FIELD, SUBJECTS_VOCABULARY_NAME);
        
        
        // FileFormat
        this.writeFileFormat(jsonGenerator, document,  FILE_FORMAT_DENORMALIZED_FIELD,MIME_TYPE_VOCABULARY_NAME, FILE_FORMAT_VOCABULARY_NAME);
 
        
    }


    

    /**
     * Write file format.
     *
     * @param jsonGenerator the json generator
     * @param document the document
     * @param denormalizedField the denormalized field
     * @param mimeTypeVocabularyName the mime type vocabulary name
     * @param formatVocabularyName the format vocabulary name
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeFileFormat(JsonGenerator jsonGenerator, DocumentModel document, String denormalizedField, String mimeTypeVocabularyName,
            String formatVocabularyName) throws IOException {


        String mimeType = null;
        String extension = null;

        // Get mime type and extension
        if (document.hasSchema(ToutaticeNuxeoStudioConst.CST_DOC_FILE_SCHEMA)) {
            if (document != null) {
                BlobHolder bHolder = document.getAdapter(BlobHolder.class);
                if( bHolder != null && bHolder.getBlob() != null)    {
                    mimeType = bHolder.getBlob().getMimeType();
                    int iExtension = bHolder.getBlob().getFilename().lastIndexOf('.');
                    if( iExtension != -1) {
                        extension = bHolder.getBlob().getFilename().substring(iExtension+1);
                    }
                }
            }
        }

        if (StringUtils.isNotEmpty(mimeType)) {

            // Get format code
            String fileFormat = null;
            Map<String, VocabularyEntry> mimeTypeVocabulary = this.getVocabulary(mimeTypeVocabularyName);
            if (MapUtils.isNotEmpty(mimeTypeVocabulary)) {
                // Search by full mime type
                VocabularyEntry entry = mimeTypeVocabulary.get(mimeType);
                if (entry != null) {
                    fileFormat = entry.getLabel();
                }   else    {
                    // Search by type
                    int iSlash = mimeType.indexOf('/');
                    if( iSlash != -1)   {
                        String type = mimeType.substring(0, iSlash+1);
                        VocabularyEntry typeEntry = mimeTypeVocabulary.get(type);
                        if (typeEntry != null) {
                            fileFormat = typeEntry.getLabel();
                        }
                    }
                }
            }

            String textDenormalization=null;
            
            if (StringUtils.isNotEmpty(fileFormat)) {

                // write format
                jsonGenerator.writeStringField(denormalizedField, fileFormat);

                // Get format text
                Map<String, VocabularyEntry> formatVocabulary = this.getVocabulary(formatVocabularyName);
                if (MapUtils.isNotEmpty(formatVocabulary) && (StringUtils.isNotEmpty(fileFormat))) {
                    // Vocabulary entry
                    VocabularyEntry entry = formatVocabulary.get(fileFormat);

                    if (entry != null) {
                        String value = entry.getLabel();
                        textDenormalization = value; 
                    }
                }
            }
            
            
            if( textDenormalization == null && StringUtils.isNotEmpty(extension)) {
                textDenormalization = FILE_LABEL+ " " + extension.toUpperCase();
            }   
                
            if( textDenormalization == null)    {
                textDenormalization = FILE_LABEL;
            }
                    
            
            jsonGenerator.writeStringField(denormalizedField + "Text", textDenormalization);
        }
    }
    
    
    /**
     * Write tree.
     * 
     * @param jsonGenerator JSON generator
     * @param document document
     * @param xpath xpath
     * @param denormalizedField denormalized field
     * @param vocabularyName vocabulary name
     * @throws IOException
     */
    private void writeTree(JsonGenerator jsonGenerator, DocumentModel document, String xpath, String denormalizedField, String vocabularyName)
            throws IOException {
        // Vocabulary
        Map<String, VocabularyEntry> vocabulary = this.getVocabulary(vocabularyName);
        // Property
        Property property = document.getProperty(xpath);

        if (MapUtils.isNotEmpty(vocabulary) && (property != null) && property.isList()) {
            jsonGenerator.writeArrayFieldStart(denormalizedField);

            // Property values
            List<?> values = property.getValue(List.class);

            for (Object value : values) {
                // Tree
                List<String> tree = new ArrayList<>();

                // Vocabulary entry
                VocabularyEntry entry = vocabulary.get(value);

                while (entry != null) {
                    tree.add(0, entry.getId());

                    // Loop on parent
                    if (StringUtils.isEmpty(entry.getParent())) {
                        entry = null;
                    } else {
                        entry = vocabulary.get(entry.getParent());
                    }
                }

                if (CollectionUtils.isNotEmpty(tree)) {
                    jsonGenerator.writeString(StringUtils.join(tree, "/"));
                }
            }

            jsonGenerator.writeEndArray();
        }
    }


    /**
     * Get vocabulary.
     * 
     * @param vocabularyName vocabulary name.
     * @return vocabulary
     */
    private Map<String, VocabularyEntry> getVocabulary(String vocabularyName) {
        if (this.vocabularies == null) {
            this.initializeVocabularies();
        }

        return this.vocabularies.get(vocabularyName);
    }


    /**
     * Initialize vocabularies.
     */
    private synchronized void initializeVocabularies() {
        if (this.vocabularies == null) {
            // Directory service
            DirectoryService directoryService = Framework.getService(DirectoryService.class);

            // Vocabulary names
            String[] vocabularyNames = new String[]{LEVELS_VOCABULARY_NAME, SUBJECTS_VOCABULARY_NAME, FILE_FORMAT_VOCABULARY_NAME, MIME_TYPE_VOCABULARY_NAME};

            // Vocabularies
            this.vocabularies = new ConcurrentHashMap<>(vocabularyNames.length);

            for (String vocabularyName : vocabularyNames) {
                this.initializeVocabulary(directoryService, vocabularyName);
            }
        }
    }


    /**
     * Initialize vocabulary.
     * 
     * @param directoryService directory service
     * @param vocabularyName vocabulary name
     */
    private void initializeVocabulary(DirectoryService directoryService, String vocabularyName) {
        // Directory schema
        String schema = directoryService.getDirectorySchema(vocabularyName);
        
        // Directory session
        Session directorySession = null;

        try {
            directorySession = directoryService.open(vocabularyName);

            Map<String, Serializable> filter = new HashMap<>(0);
            DocumentModelList documents = directorySession.query(filter);

            // Vocabulary
            Map<String, VocabularyEntry> vocabulary;
            
            if (CollectionUtils.isEmpty(documents)) {
                vocabulary = new ConcurrentHashMap<>(0);
            } else {
                vocabulary = new ConcurrentHashMap<>(documents.size());
                
                for (DocumentModel document : documents) {
                    String id = (String) document.getProperty(schema, "id");
                    String parent = (String) document.getProperty(schema, "parent");
                    String label = (String) document.getProperty(schema, "label");

                    if (StringUtils.isNotEmpty(id)) {
                        // Vocabulary entry
                        VocabularyEntry entry = new VocabularyEntry();
                        entry.setId(id);
                        entry.setParent(parent);
                        entry.setLabel(label);

                        vocabulary.put(id, entry);
                    }
                }
            }

            this.vocabularies.put(vocabularyName, vocabulary);
        } catch (DirectoryException e) {
            this.log.error(e.getMessage(), e.getCause());
        } finally {
            if (directoryService != null) {
                try {
                    directorySession.close();
                } catch (DirectoryException e) {
                    this.log.error(e.getMessage(), e.getCause());
                }
            }
        }
    }


    /**
     * Vocabulary entry.
     * 
     * @author Cédric Krommenhoek
     */
    private class VocabularyEntry {

        /** Identifier. */
        private String id;
        
        /** Label. */
        private String label;
        


        /** Parent. */
        private String parent;


        /**
         * Constructor.
         */
        public VocabularyEntry() {
            super();
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
         * Getter for parent.
         * 
         * @return the parent
         */
        public String getParent() {
            return parent;
        }

        /**
         * Setter for parent.
         * 
         * @param parent the parent to set
         */
        public void setParent(String parent) {
            this.parent = parent;
        }
        
        
        /**
         * Getter for label.
         * @return the label
         */
        public String getLabel() {
            return label;
        }


        
        /**
         * Setter for label.
         * @param label the label to set
         */
        public void setLabel(String label) {
            this.label = label;
        }

    }

}
