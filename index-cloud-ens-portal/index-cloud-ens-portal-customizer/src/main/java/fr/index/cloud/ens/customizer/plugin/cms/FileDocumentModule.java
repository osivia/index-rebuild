package fr.index.cloud.ens.customizer.plugin.cms;

import fr.toutatice.portail.cms.nuxeo.api.ContextualizationHelper;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoPublicationInfos;
import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import fr.toutatice.portail.cms.nuxeo.api.domain.RemotePublishedDocumentDTO;
import fr.toutatice.portail.cms.nuxeo.api.portlet.PortletModule;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoCustomizer;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.cms.FileMimeType;
import org.osivia.portal.api.cms.Permissions;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.api.windows.PortalWindow;
import org.osivia.portal.api.windows.WindowFactory;

import javax.portlet.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * File document module.
 *
 * @author Cédric Krommenhoek
 * @see PortletModule
 */
public class FileDocumentModule extends PortletModule {

    /**
     * Mutualized space path.
     */
    private static final String MUTUALIZED_SPACE_PATH = System.getProperty("config.mutualized.path");

    
    /** Logger. */
    private static final Log LOGGER = LogFactory.getLog(FileDocumentModule.class);

    /**
     * Portal URL factory.
     */
    private final IPortalUrlFactory portalUrlFactory;

    /**
     * Document DAO.
     */
    private final DocumentDAO documentDao;


    /**
     * Constructor.
     *
     * @param portletContext portlet context
     */
    public FileDocumentModule(PortletContext portletContext) {
        super(portletContext);

        // Portal URL factory
        this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        // Document DAO
        this.documentDao = DocumentDAO.getInstance();
    }


    @Override
    protected void doView(RenderRequest request, RenderResponse response, PortletContext portletContext) throws PortletException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(portletContext, request, response);
        // Nuxeo controller
        NuxeoController nuxeoController = new NuxeoController(portalControllerContext);

        // Document path
        String path = getDocumentPath(nuxeoController);

        try {

            if (StringUtils.isNotBlank(path)) {
                // Document context
                NuxeoDocumentContext documentContext = nuxeoController.getDocumentContext(path);
                // Document
                Document document = documentContext.getDocument();
                // Publication infos
                NuxeoPublicationInfos publicationInfos = documentContext.getPublicationInfos();
                
                
                request.setAttribute("baseUrl", "https://" + request.getServerName());


                if (publicationInfos.isPublished()) {
                    // Read only indicator
                    request.setAttribute("readOnly", true);

                    // Copy
                    String copyUrl = this.getCopyUrl(portalControllerContext, path);
                    request.setAttribute("copyUrl", copyUrl);


                    if (!StringUtils.equals(document.getProperties().getString("dc:lastContributor"), request.getRemoteUser()))
                        request.setAttribute("contactAuthor", true);



                } else if (ContextualizationHelper.isCurrentDocContextualized(portalControllerContext)) {
                    // Permissions
                    Permissions permissions = documentContext.getPermissions();

                    // Mutualize URL
                    String mutualizeUrl = this.getMutualizeUrl(portalControllerContext, path);
                    request.setAttribute("mutualizeUrl", mutualizeUrl);

                    if (permissions.isEditable()) {
                        // Rename URL
                        String renameUrl = this.getRenameUrl(portalControllerContext, path);
                        request.setAttribute("renameUrl", renameUrl);

                        // Edit URL
                        String editUrl = this.getEditUrl(portalControllerContext, nuxeoController, path);
                        request.setAttribute("editUrl", editUrl);
                    }

                    if (permissions.isDeletable()) {
                        // Delete URL
                        String deleteUrl = this.getDeleteUrl(portalControllerContext, path);
                        request.setAttribute("deleteUrl", deleteUrl);
                    }

                    // Desynchronized and format indicator
                    this.applyPublicationRules(portalControllerContext, documentContext);


                    // Source
                    DocumentDTO source = this.getSource(nuxeoController, documentContext);
                    request.setAttribute("source", source);

                    if (source != null) {
                        boolean desynchronizeFromSource = isDesynchronizedFromSource(nuxeoController, documentContext, source);
                        request.setAttribute("desynchronizedFromSource", desynchronizeFromSource);
                    }   

               }

                
                

                getMimeTypeSpecificLinks(request, nuxeoController, documentContext);
            }
        } catch (Exception e) {
            throw new PortletException(e);
        }
    }


    /**
     * Gets the mime type specific links.
     *
     * @param request the request
     * @param nuxeoController the nuxeo controller
     * @param documentContext the document context
     * @return the mime type specific links
     */
    private void getMimeTypeSpecificLinks(RenderRequest request, NuxeoController nuxeoController, NuxeoDocumentContext documentContext) {
        Document document = documentContext.getDocument();
        if ("File".equals(documentContext.getDocument().getType())) {

            PropertyMap map = document.getProperties().getMap("file:content");

            String mimeType = map.getString("mime-type");
            if ( StringUtils.contains(mimeType, "application/index-qcm+xml")) {

                String createFileLink = nuxeoController.createFileLink(document, "file:content");
                try {
                    createFileLink = URLEncoder.encode(createFileLink, "UTF-8");
                    request.setAttribute("QCMUrl", createFileLink);
                } catch (UnsupportedEncodingException e) {
                    // Don't block on a link
                    LOGGER.error("Link on " + map.getString("name") + " generates " + e.getMessage());
                }
            }

        }
    }


    /**
     * Get mutualize URL.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return URL
     */
    private String getMutualizeUrl(PortalControllerContext portalControllerContext, String path) throws PortletException {
        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put("osivia.mutualize.path", path);

        // URL
        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, "index-cloud-ens-mutualization-instance", properties, PortalUrlType.MODAL);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    /**
     * Get copy URL.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return URL
     */
    private String getCopyUrl(PortalControllerContext portalControllerContext, String path) throws PortletException {
        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put("osivia.copy.path", path);

        // URL
        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, "index-cloud-ens-mutualization-copy-instance", properties,
                    PortalUrlType.MODAL);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    /**
     * Get rename URL.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return URL
     */
    private String getRenameUrl(PortalControllerContext portalControllerContext, String path) throws PortletException {
        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put("osivia.rename.path", path);

        // URL
        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, "osivia-services-widgets-rename-instance", properties,
                    PortalUrlType.MODAL);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    /**
     * Get edit URL.
     *
     * @param portalControllerContext portal controller context
     * @param nuxeoController         Nuxeo controller
     * @param path                    document path
     * @return URL
     */
    private String getEditUrl(PortalControllerContext portalControllerContext, NuxeoController nuxeoController, String path) throws PortletException {
        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put("osivia.document.edition.base-path", nuxeoController.getBasePath());
        properties.put("osivia.document.edition.path", path);

        // URL
        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, "osivia-services-document-edition-instance", properties,
                    PortalUrlType.MODAL);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    /**
     * Get delete URL.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     * @return URL
     */
    private String getDeleteUrl(PortalControllerContext portalControllerContext, String path) throws PortletException {
        // Window properties
        Map<String, String> properties = new HashMap<>();
        properties.put("osivia.delete.path", path);

        // URL
        String url;
        try {
            url = this.portalUrlFactory.getStartPortletUrl(portalControllerContext, "osivia-services-widgets-delete-instance", properties, PortalUrlType.MODAL);
        } catch (PortalException e) {
            throw new PortletException(e);
        }

        return url;
    }


    /**
     * Application publication rules
     *  - Check if mutualized document is desynchronized.
     *  - get publication format
     *
     * @param portalControllerContext portal controller context
     * @param documentContext         document context
     * @return true if document is desynchronized
     */
    private boolean applyPublicationRules(PortalControllerContext portalControllerContext, NuxeoDocumentContext documentContext) throws PortletException {
        // Portlet request
        PortletRequest request = portalControllerContext.getRequest();

        // Document
        DocumentDTO document = (DocumentDTO) request.getAttribute("document");

        // Published documents
        List<RemotePublishedDocumentDTO> publishedDocuments;
        if (document == null) {
            publishedDocuments = null;
        } else {
            publishedDocuments = document.getPublishedDocuments();
        }

        // Mutualized document
        RemotePublishedDocumentDTO mutualizedDocument = null;
        if (CollectionUtils.isNotEmpty(publishedDocuments)) {
            Iterator<RemotePublishedDocumentDTO> iterator = publishedDocuments.iterator();
            while ((mutualizedDocument == null) && iterator.hasNext()) {
                RemotePublishedDocumentDTO publishedDocument = iterator.next();

                // Published document path
                String publishedDocumentPath = publishedDocument.getPath();
                if (!StringUtils.startsWith(publishedDocumentPath, "/")) {
                    publishedDocumentPath = "/" + publishedDocumentPath;
                }

                if (StringUtils.startsWith(publishedDocumentPath, MUTUALIZED_SPACE_PATH)) {
                    mutualizedDocument = publishedDocument;
                }
            }
        }


        // Desynchronized indicator
        boolean desynchronized = false;
        DocumentDTO publicationDocument = null;
        String publicationFormat = null;

        if (mutualizedDocument != null) {

            NuxeoController nuxeoController = new NuxeoController(portalControllerContext);
            nuxeoController.setDisplayLiveVersion("0");
            
         
            
            /* check if digest is different */
            
            String publishedDocumentPath = mutualizedDocument.getPath();
            if (!StringUtils.startsWith(publishedDocumentPath, "/")) {
                publishedDocumentPath = "/" + publishedDocumentPath;
            }
            
            NuxeoDocumentContext mutualizedDocumentContext = nuxeoController.getDocumentContext(publishedDocumentPath);
            Document mutualizedDoc = mutualizedDocumentContext.getDenormalizedDocument();
            
            
            if(!StringUtils.equals(getDigest(document.getDocument()),getDigest( mutualizedDoc))) {
                desynchronized = true;
            }

            /* Get format */
    
            publicationFormat = mutualizedDoc.getProperties().getString("idxcl:formatText");


            publicationDocument = this.documentDao.toDTO(mutualizedDoc);
         }
        
        if( publicationFormat != null) 
            request.setAttribute("publicationFormat", publicationFormat); 
        
        if( publicationDocument != null)
            request.setAttribute("publicationDocument", publicationDocument);     
        
        
        request.setAttribute("desynchronized", desynchronized);

        return desynchronized;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    

    /**
     * Get source document DTO.
     *
     * @param nuxeoController Nuxeo controller
     * @param documentContext document context
     * @return document DTO
     */
    private DocumentDTO getSource(NuxeoController nuxeoController, NuxeoDocumentContext documentContext) {
        // Document
        Document document = documentContext.getDocument();

        // Source webId
        String sourceWebId = document.getString("mtz:sourceWebId");

        // Source
        DocumentDTO source;
        if (StringUtils.isEmpty(sourceWebId)) {
            source = null;
        } else {
            // Nuxeo command
            INuxeoCommand command = new GetSourceByWebIdCommand(sourceWebId);
            Documents documents = (Documents) nuxeoController.executeNuxeoCommand(command);

            if (documents.size() == 1) {
                source = this.documentDao.toDTO(documents.get(0));
            } else {
                source = null;
            }
        }

        return source;
    }

    
    
    private boolean  isDesynchronizedFromSource( NuxeoController nuxeoController, NuxeoDocumentContext documentContext, DocumentDTO source)  {
        boolean desynchronized = false;
        
        Document document = documentContext.getDocument();
  
        // Check version
        if( !StringUtils.equals(document.getProperties().getString("mtz:sourceVersion"), source.getDocument().getVersionLabel() ))   {
            // Check digest
            if( !StringUtils.equals(document.getProperties().getString("mtz:sourceDigest"), getDigest(source.getDocument()))) {
                desynchronized = true;
            }
        }
        return desynchronized;
     }

    
    private String getDigest( Document source) {
        String sourceDigest = null;
        PropertyMap sourceMap = source.getProperties().getMap("file:content");
        if( sourceMap != null)
         sourceDigest = sourceMap.getString("digest");    
        return sourceDigest;

    }

    
    /**
     * Get document path.
     *
     * @param nuxeoController Nuxeo controller
     * @return path
     */
    private String getDocumentPath(NuxeoController nuxeoController) {
        // Current window
        PortalWindow window = WindowFactory.getWindow(nuxeoController.getRequest());

        return nuxeoController.getComputedPath(window.getProperty(Constants.WINDOW_PROP_URI));
    }

}
