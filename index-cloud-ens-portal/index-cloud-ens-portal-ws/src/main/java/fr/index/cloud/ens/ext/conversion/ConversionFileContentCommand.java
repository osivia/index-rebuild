
package fr.index.cloud.ens.ext.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.spi.StreamedSession;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.nuxeo.ecm.automation.client.model.StreamBlob;
import org.osivia.portal.core.cms.CMSBinaryContent;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoCompatibility;

/**
 * File content command.
 *
 * @see INuxeoCommand
 */
public class ConversionFileContentCommand implements INuxeoCommand {


    Document document;


    public ConversionFileContentCommand(Document document) {
        super();
        this.document = document;

    }

    public ConversionFileContentCommand(String docPath, String fieldName) {
        super();
        this.document = null;

    }

    public static PropertyMap getFileMap(Document nuxeoDocument, String fieldName) {

        PropertyMap map = nuxeoDocument.getProperties().getMap(fieldName);

        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session session) throws Exception {

        PropertyMap map = getFileMap(document, "file:content");

        String pathFile = map.getString("data");


        // download the file from its remote location
        FileBlob fileBlob = (FileBlob) session.getFile(pathFile);


        // return blob

        return fileBlob;

    };


    @Override
    public String getId() {
        String id = "ConversionFileContentCommand";
        id += this.document;

        return id ;
    };


}
