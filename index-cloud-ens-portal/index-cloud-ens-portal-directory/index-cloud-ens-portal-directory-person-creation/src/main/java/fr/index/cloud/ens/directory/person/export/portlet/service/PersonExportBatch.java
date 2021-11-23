/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.portlet.PortletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.batch.AbstractBatch;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.CMSBinaryContent;

import fr.index.cloud.ens.directory.person.export.portlet.commands.ExportWorkspaceCommand;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterException;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoServiceFactory;

/**
 * @author Loïc Billon
 *
 */
// @Component
public class PersonExportBatch extends AbstractBatch {

    private final static Log logger = LogFactory.getLog("batch");


    /** Portlet context. */
    private static PortletContext portletContext;

    /**
     * Zip file name RegEx.
     */
    private static final String ZIP_FILE_NAME_REGEX = "(.+) \\(([0-9]+)\\)";
    /**
     * Zip file name pattern.
     */
    private final Pattern zipFileNamePattern;

    private final String userWorkspacePath;

    private String taskPath;

    private String exportsPath;

    
    private String batchId;
    /**
     * 
     */
    public PersonExportBatch(String userWorkspacePath) {
        // Zip file name pattern
        this.zipFileNamePattern = Pattern.compile(ZIP_FILE_NAME_REGEX);
        this.userWorkspacePath = userWorkspacePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osivia.portal.api.batch.AbstractBatch#getJobScheduling()
     */
    @Override
    public String getJobScheduling() {
        // Start immediatly one shot
        return null;
    }

    
    @Override
    public boolean isRunningOnMasterOnly() {
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.osivia.portal.api.batch.AbstractBatch#execute(java.util.Map)
     */
    @Override
    public void execute(Map<String, Object> parameters) throws PortalException {

        NuxeoController nuxeoController = new NuxeoController(portletContext);

        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);

        // Nuxeo command
        INuxeoCommand command = new ExportWorkspaceCommand(userWorkspacePath);
        nuxeoController.setStreamingSupport(true);
        nuxeoController.setForcePublicationInfosScope("superuser_context");

        Documents documents = (Documents) nuxeoController.executeNuxeoCommand(command);

        // Make zip file
        String zipFilename = buildZip(nuxeoController, documents);

        // Update procedure
        IFormsService formsService = NuxeoServiceFactory.getFormsService();
        Map<String, String> variables = new HashMap<>();
        variables.put("zipFilename", zipFilename);


        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext);

        NuxeoDocumentContext task = nuxeoController.getDocumentContext(taskPath);

        try {
            formsService.proceed(portalControllerContext, task.getDenormalizedDocument(), variables);
        } catch (FormFilterException e) {
            throw new PortalException(e);
        }

    }

    private String buildZip(NuxeoController nuxeoController, Documents documents) throws PortalException {

        String zipFilename = null;

        // ----------------------------
        List<CMSBinaryContent> contents = new ArrayList<>();
        Map<String, String> folders = new HashMap<>();

        for (Document doc : documents) {
            if (doc.getType().equals("Folder")) {

                // Ajout du titre du parent dans le nom du dossier zip si connu
                String parent = StringUtils.substringBeforeLast(doc.getPath(), "/");

                String fullTitle = doc.getTitle() + "/";
                if (folders.get(parent) != null) {
                    fullTitle = folders.get(parent) + fullTitle;
                }
                folders.put(doc.getPath(), fullTitle);


            } else {

                // Binary contents
                CMSBinaryContent content = nuxeoController.fetchFileContent(doc.getPath(), "file:content");

                // Ajout du titre du parent dans le nom du dossier zip si connu
                String parent = StringUtils.substringBeforeLast(doc.getPath(), "/");
                String fullTitle = doc.getTitle();
                if (folders.get(parent) != null) {
                    fullTitle = folders.get(parent) + fullTitle;
                }
                content.setName(fullTitle);

                logger.debug("get : " + doc.getPath() + " and set as name " + fullTitle);

                contents.add(content);

            }
        }

        // === Binary contents
        try {
            // Zip file
            File zipFile = File.createTempFile("file-browser-bulk-download-", ".tmp");
            zipFilename = zipFile.getName();

            // Zip output stream
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
            zipOutputStream.setLevel(Deflater.NO_COMPRESSION);

            // Counting output stream
            CountingOutputStream countingOutputStream = new CountingOutputStream(zipOutputStream);

            // Zip file names
            Set<String> zipFileNames = new HashSet<>();

            try {

                for (String folder : folders.values()) {
                    ZipEntry zipEntry = new ZipEntry(folder);
                    zipOutputStream.putNextEntry(zipEntry);
                }

                for (CMSBinaryContent content : contents) {
                    // File name ; must be unique
                    String fileName = content.getName();

                    logger.debug("Compress : " + fileName);

                    while (zipFileNames.contains(fileName)) {
                        String name = StringUtils.substringBeforeLast(fileName, ".");
                        int counter;
                        String extension = StringUtils.substringAfterLast(fileName, ".");

                        // Matcher
                        Matcher matcher = this.zipFileNamePattern.matcher(name);
                        if (matcher.matches()) {
                            name = matcher.group(1);
                            counter = NumberUtils.toInt(matcher.group(2), 0);
                        } else {
                            counter = 0;
                        }

                        StringBuilder builder = new StringBuilder();
                        builder.append(name);
                        builder.append(" (");
                        builder.append(counter + 1);
                        builder.append(").");
                        builder.append(extension);

                        fileName = builder.toString();
                    }
                    zipFileNames.add(fileName);

                    // Zip entry
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipEntry.setSize(content.getFileSize());
                    zipEntry.setCompressedSize(-1);

                    // Buffer
                    byte[] buffer = new byte[1000000];

                    if (content.getFile() != null) {
                        File file = content.getFile();
                        zipEntry.setTime(file.lastModified());

                        FileInputStream fileInputStream = new FileInputStream(file);

                        // CRC
                        CheckedInputStream checkedInputStream = new CheckedInputStream(fileInputStream, new CRC32());
                        try {
                            while (checkedInputStream.read(buffer) >= 0) {
                            }
                            zipEntry.setCrc(checkedInputStream.getChecksum().getValue());

                            zipOutputStream.putNextEntry(zipEntry);
                        } finally {
                            IOUtils.closeQuietly(checkedInputStream);
                        }

                        // Write
                        fileInputStream = new FileInputStream(file);
                        try {
                            int i = -1;
                            while ((i = fileInputStream.read(buffer)) != -1) {
                                countingOutputStream.write(buffer, 0, i);
                            }
                            countingOutputStream.flush();
                        } finally {
                            IOUtils.closeQuietly(fileInputStream);
                        }
                    } else if (content.getStream() != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        // CRC
                        CheckedInputStream checkedInputStream = new CheckedInputStream(content.getStream(), new CRC32());
                        try {
                            int i = -1;
                            while ((i = checkedInputStream.read(buffer)) != -1) {
                                byteArrayOutputStream.write(buffer, 0, i);
                            }
                            zipEntry.setCrc(checkedInputStream.getChecksum().getValue());

                            zipOutputStream.putNextEntry(zipEntry);
                        } finally {
                            IOUtils.closeQuietly(checkedInputStream);
                        }

                        // Write
                        try {
                            byteArrayOutputStream.writeTo(countingOutputStream);
                            countingOutputStream.flush();
                        } finally {
                            IOUtils.closeQuietly(byteArrayOutputStream);
                        }
                    } else {
                        continue;
                    }
                }
            } finally {
                IOUtils.closeQuietly(countingOutputStream);
            }

            // Move to storage directory
            Files.move(Paths.get(zipFile.getAbsolutePath()), Paths.get(exportsPath + zipFilename));

            // Nuxeo controller
        } catch (IOException ex) {
            throw new PortalException(ex);
        }

        return zipFilename;
    }

    public void setPortletContext(PortletContext portletContext) {
        PersonExportBatch.portletContext = portletContext;
    }

    /**
     * @return the taskPath
     */
    public String getTaskPath() {
        return taskPath;
    }

    /**
     * @param taskPath the taskPath to set
     */
    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }

    /**
     * @param exportsPath the exportsPath to set
     */
    public void setExportsPath(String exportsPath) {
        this.exportsPath = exportsPath;
    }

    @Override
    public String getBatchId() {
        if (batchId != null)
            return batchId;
        else
            return super.getBatchId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osivia.portal.api.batch.Batch#setBatchId()
     */
    @Override
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
