package fr.index.cloud.ens.metadata;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.automation.core.util.Properties;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;

import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;


/**
 * Update publication informations in silent mode
 * 
 * @author jssteux
 */
@Operation(
        id = UpdateMetadata.ID,
        category = Constants.CAT_DOCUMENT,
        label = "Update Index Cloud meta data in silent mode",
        description = "Create an entry in the \"rshr:targets complex property value on the input document. The document is automatically saved if 'save' parameter is true. If you unset the 'save' you need to save it later using Save Document operation. Return the modified document.")

public class UpdateMetadata {
    public static final String ID = "Index.UpdateMetadata";
    
    private static final String TARGET_XPATH = "rshr:targets";

    @Context
    protected CoreSession session;

    @Param(name = "properties", required = false)
    protected Properties properties=null;
    
    
    @Param(name = "targetValue", required = false)
    protected Properties targetValue=null;
    
    @Param(name = "targetIndex", required = false)
    protected String targetIndex=null;
    
    @Param(name = "targetAction", required = false)
    protected String targetAction=null;   
    
    @Param(name = "removeFormat", required = false)
    protected boolean removeFormat = false;
    
    
    @Param(name = "save", required = false, values = "true")
    protected boolean save = true;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {

        DocumentModel updatedDoc = execute(this.session, doc, this.properties, targetAction, targetIndex, targetValue, this.save);
        return updatedDoc;
    }

    
    
    /**
     * Update document silently.
     * 
     * @param session
     * @param document
     * @param properties
     * @param dublinCoreProperties
     * @return document
     * @throws ClientException
     * @throws IOException
     * @throws DocumentException 
     */

    protected DocumentModel execute(CoreSession session, DocumentModel document, Properties properties,  String targetAction, String targetIndex, Properties targetValue, boolean save) throws ClientException, IOException, DocumentException {

        // In silent mode, Document must be checkouted explicitly
        // otherwise publication erases modification (probably during checking)
        
        if( !document.isCheckedOut())
            document.checkOut();
        
        
        if( properties != null) {
            DocumentHelper.setProperties(session, document, properties);

        }
        
        if( removeFormat) {
            // Remove comple property
            DocumentHelper.removeProperty(document,  "rshr:format");           
        }
        
        
        if( "publish".equals(targetAction)) {
            // Add complex property target
            Property property = document.getProperty(TARGET_XPATH);

            if (property != null) {
                Serializable value2 = property.getValue();

                if (value2 instanceof Serializable && value2 instanceof List<?>) {
                    List<Map<String, Object>> complexList = (List<Map<String, Object>>) value2;
                    
                    HashMap<String, Object> targetMap = new HashMap<String, Object>(targetValue);

                    // Need to convert from string to arrayList
                    
                    String pubGroups = targetValue.get("pubGroups");
                    if( pubGroups != null) {
                        List<String> normalizedPubGroups = new ArrayList<>();
                        String[] toks = pubGroups.split(",");
                        for(int i=0;i < toks.length; i++)   {
                            normalizedPubGroups.add(toks[i]);
                        }
                        targetMap.put("pubGroups", normalizedPubGroups);
                        
                    }
                    
                    complexList.add(targetMap);

                    document.setPropertyValue(TARGET_XPATH, (Serializable) complexList);
                } else {
                    throw new DocumentException("the value is not a Serializable List " + targetValue);
                }

            } else {
                throw new DocumentException("no property with name " + property);
            }            
        }
        
        if( "unpublish".equals(targetAction)) {
            // Remove comple property
            DocumentHelper.removeProperty(document, TARGET_XPATH +"/" + targetIndex);
       }
       
        
        if(save){
            ToutaticeDocumentHelper.saveDocumentSilently(session, document, false);
        }
        
        return document;
    }
    
    
}
