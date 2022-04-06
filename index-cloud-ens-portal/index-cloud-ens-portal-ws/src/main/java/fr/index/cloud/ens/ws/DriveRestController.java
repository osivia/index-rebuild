package fr.index.cloud.ens.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.tokens.ITokenService;
import org.osivia.portal.core.cms.CMSBinaryContent;
import org.osivia.portal.core.web.IWebIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import fr.index.cloud.ens.conversion.admin.ConversionAdminService;
import fr.index.cloud.ens.ext.conversion.ConversionRecord;
import fr.index.cloud.ens.ext.conversion.ConversionRepository;
import fr.index.cloud.ens.ext.conversion.IConversionService;
import fr.index.cloud.ens.ws.beans.CreateFolderBean;
import fr.index.cloud.ens.ws.beans.CreateUserBean;
import fr.index.cloud.ens.ws.beans.GetSharedUrlBean;
import fr.index.cloud.ens.ws.beans.PublishBean;
import fr.index.cloud.ens.ws.beans.MetadataClassifier;
import fr.index.cloud.ens.ws.beans.DocumentProperties;
import fr.index.cloud.ens.ws.beans.UnpublishBean;
import fr.index.cloud.ens.ws.beans.UpdatedProperties;
import fr.index.cloud.ens.ws.beans.UploadBean;
import fr.index.cloud.ens.ws.beans.UserStorageBean;
import fr.index.cloud.ens.ws.check.GlobalChecker;
import fr.index.cloud.ens.ws.commands.AddPropertiesCommand;
import fr.index.cloud.ens.ws.commands.CreateFolderCommand;
import fr.index.cloud.ens.ws.commands.FetchByPubIdCommand;
import fr.index.cloud.ens.ws.commands.FetchByTitleCommand;
import fr.index.cloud.ens.ws.commands.FolderGetChildrenCommand;
import fr.index.cloud.ens.ws.commands.GetByPathCommand;
import fr.index.cloud.ens.ws.commands.GetHierarchyCommand;
import fr.index.cloud.ens.ws.commands.GetQuotaCommand;
import fr.index.cloud.ens.ws.commands.GetSharedUrlCommand;
import fr.index.cloud.ens.ws.commands.GetUserProfileCommand;
import fr.index.cloud.ens.ws.commands.PublishCommand;
import fr.index.cloud.ens.ws.commands.SearchCommand;
import fr.index.cloud.ens.ws.commands.UnpublishCommand;
import fr.index.cloud.ens.ws.commands.UploadFileCommand;
import fr.index.cloud.oauth.config.SecurityFilter;
import fr.index.cloud.oauth.tokenStore.AggregateRefreshTokenInfos;
import fr.index.cloud.oauth.tokenStore.PortalRefreshTokenAuthenticationDatas;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;
import fr.toutatice.portail.cms.nuxeo.api.ResourceUtil;
import fr.toutatice.portail.cms.nuxeo.api.cms.NuxeoDocumentContext;
import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;
import fr.toutatice.portail.cms.nuxeo.api.services.dao.DocumentDAO;
import net.sf.json.JSONObject;


/**
 * Services Rest associés au Drive
 * 
 * @author Jean-Sébastien
 */
@RestController
public class DriveRestController {


    private static final String FILE_NAME_PATTERN = "([^/\\\\:*?\\\"<>|])*";
    private static final String PROP_TTC_WEBID = "ttc:webid";
    private static final String PROP_SHARE_LINK = "rshr:linkId";
    private static final String PROP_ENABLE_LINK = "rshr:enabledLink";

    public static final String SHARE_URL_PREFIX = "/s/";
    public static final int ERR_CREATE_USER_MAIL_ALREADYEXIST = 1;

    public final static String DEFAULT_FORMAT = "default";
    public final static String NATIVE_FORMAT = "native";
    public final static String PDF_FORMAT = "pdf";

    public static final String DRIVE_TYPE_FILE = "file";
    public static final String DRIVE_TYPE_FOLDER = "folder";
    public static final String DRIVE_TYPE_ROOT = "root";

    public static final String FOLDER_SEPARATOR = "/";
    public static final String PATH_SEPARATOR = "/";
    
    public static final int ERR_UPLOAD_INVALID_ACTION = 1;
    public static final int ERR_UPLOAD_INVALID_CHARACTER = 2;

    public static final int ERR_CREATE_FOLDER_ALREADY_EXISTS = 1;
    public static final int ERR_CREATE_FOLDER_EMPTY_NAME = 2;   
    
    public static final int ERR_SEARCH_INCORRECT_PATH = 1;
    public static final int ERR_SEARCH_INCORRECT_TYPE = 2;   

    public static PortletContext portletContext;
    
    /** Logger. */
    private static final Log logger = LogFactory.getLog(DriveRestController.class);


    Map<String, String> levelQualifier = null;


    @Autowired
    @Qualifier("personUpdateService")
    private PersonUpdateService personUpdateService;

    @Autowired
    private ITokenService tokenService;
    
    @Autowired
    ConversionRepository conversionRepository;


    @Autowired
    private ErrorMgr errorMgr;

    /**
     * Document DAO.
     */
    @Autowired
    IConversionService conversionService;
    


    /**
     * Wraps the doc. fetching according to the WS error handling
     * 
     * (Nuxeo Exception are converted into GenericException with the {id} of the content
     * added).
     * 
     * @param ctl
     * @param path
     * @return
     */
    public static Document wrapContentFetching(NuxeoController nuxeoController, String path) throws GenericException {
        Document currentDoc = null;
        try {
            currentDoc = nuxeoController.getDocumentContext(path).getDocument();
        } catch (NuxeoException e) {
            throw new GenericException(e, path);
        }
        return currentDoc;
    }


    /**
     * Get root url
     * 
     * 
     * @param ctl
     * @param path
     * @return
     */

    public static String getUrl(HttpServletRequest request) {
        String url = "https://" + request.getServerName();
        return url;
    }


    public static String getSharedUrl(HttpServletRequest request, String shareLink) {
        return getUrl(request) + SHARE_URL_PREFIX + shareLink;
    }



    
    /**
     * Get a nuxeoController associated to the current user
     * 
     * @return
     * @throws Exception
     */
    public static NuxeoController getNuxeocontroller(HttpServletRequest request, Principal principal) throws Exception {
        
        
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        
        if( logger.isDebugEnabled()) {
            if (session == null) {
                logger.debug("session is null");
            }   else    {
                logger.debug("session =" + session.getId());
                String sessionPrincipal = (String) session.getAttribute("osivia.principal");
                logger.debug("sessionPrincipal " + sessionPrincipal);
            }
        }
        
        /* On controle à minima que la session correspond bien au Principal 
         * (pb. de cookie sur l'appelant)
         */
        
        
        if (session != null && principal != null) {
            String sessionPrincipal = (String) session.getAttribute("osivia.principal");
            if ( sessionPrincipal != null && !StringUtils.equals(sessionPrincipal, principal.getName())) {
                
                logger.warn("Session is desynchronized. It is invalidated.");
                
                session.invalidate();
                session = ((HttpServletRequest) request).getSession(true);
            }
            
            session.setAttribute("osivia.principal", principal.getName());
        }


        NuxeoController nuxeoController = new NuxeoController(portletContext);
        nuxeoController.setServletRequest(request);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_USER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);

        if (principal != null) {
            request.setAttribute("osivia.delegation.userName", principal.getName());
        }

        return nuxeoController;

    }


    /**
     * URL to use: /index-cloud-portal-ens-ws/rest/Drive.content
     * 
     * Codes erreurs : NOT_FOUND, FORBIDDEN
     * 
     * @param request
     * @param id
     * @return
     * @throws Exception
     */

    private Map<String, Object> initContent(HttpServletRequest request, String rootPath, Document doc, boolean mainObject, String path) {

        Map<String, Object> contents = new LinkedHashMap<>();
 
        if (mainObject)
            contents.put("returnCode", ErrorMgr.ERR_OK);
        
        if( path != null)
            contents.put("path", path);
        
        PropertyList facets = doc.getFacets();
        
        String type;
        if (doc.getPath().equals(rootPath))
            type = DRIVE_TYPE_ROOT;
        else if (facets.list().contains("Folderish"))
            type = DRIVE_TYPE_FOLDER;
        else
            type = DRIVE_TYPE_FILE;
        
        
        contents.put("type", type);
        contents.put("id", doc.getProperties().getString(PROP_TTC_WEBID));
        contents.put("title", doc.getTitle());
        
        /* link */
        if (!facets.list().contains("Folderish"))    {
            
            PropertyMap fileContent = doc.getProperties().getMap("file:content");
            
            // MIME type
            if (fileContent != null) {
                String mimeType = fileContent.getString("mime-type");
                contents.put("mimeType",mimeType);
            }

            
            
            if (doc.getProperties().get("common:size") != null) {
                long size = doc.getProperties().getLong("common:size");
                contents.put("fileSize", size);
            }            

            Boolean enableLink = doc.getProperties().getBoolean(PROP_ENABLE_LINK, false);
            if (enableLink) {
                String shareLink = doc.getProperties().getString(PROP_SHARE_LINK);
                if (shareLink != null) {
                    contents.put("shareUrl", getSharedUrl(request, shareLink));
                }
            }
    
            boolean pdfConvertible = ispdfConvertible(doc);
            contents.put("pdfConvertible", pdfConvertible);
    
            /* format */
            String format = doc.getProperties().getString("rshr:format");
            if (StringUtils.isEmpty(format)) {
                format = DEFAULT_FORMAT;
            }
            contents.put("pubFormat", format);
    
    
            // Digest
           
            if (fileContent != null) {
                contents.put("hash", fileContent.getString("digest"));
            }

        }

        if (doc.getProperties().get("dc:modified") != null) {
            Date lastModifiedDate = doc.getProperties().getDate("dc:modified");
            contents.put("lastModified", lastModifiedDate.getTime());
        }

        return contents;
    }


    /**
     * Checks if is pdf convertible.
     *
     * @param doc the doc
     * @return true, if is pdf convertible
     */

    private boolean ispdfConvertible(Document doc) {

        DocumentDTO dto = DocumentDAO.getInstance().toDTO(doc);
        return dto.isPdfConvertible();
    }


    private Map<String, Object> initContent(HttpServletRequest request, String rootPath, Document doc) {
        return initContent(request, rootPath, doc, false, null);
    }


    @RequestMapping(value = "/Drive.webUrl", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)

    public Map<String, Object> getWebUrl(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "id", required = false) String id, Principal principal) throws Exception {

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        WSPortalControllerContext ctx = new WSPortalControllerContext(request, response, principal);

        try {
            Map<String, String> tokenAttributes = new ConcurrentHashMap<>();
            tokenAttributes.put("uid", principal.getName());
            String webToken = tokenService.generateToken(tokenAttributes);
            String url = getUrl(request) + "/"+NuxeoController.getCMSNuxeoWebContextName()+"/binary?id=" + id + "&webToken=" + webToken + "&viewer=true";
            returnObject.put("url", url);

        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(ctx, e);
        }
        return returnObject;
    }


    /**
     * Get content datas for the specified id
     * 
     * @param request
     * @param response
     * @param id
     * @param principal
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/Drive.content", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)

    public Map<String, Object> getContent(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id", required = false) String id,
            Principal principal) throws Exception {


        WSPortalControllerContext ctx = new WSPortalControllerContext(request, response, principal);
        NuxeoController nuxeoController = getNuxeocontroller(request, principal);

        Map<String, Object> returnObject;

        try {
            String rootPath = getRootPath (nuxeoController, principal);

            String path = null;
            if (id != null)
                path = IWebIdService.FETCH_PATH_PREFIX + id;
            else
                path = rootPath;

            // Get current doc
            Document currentDoc = wrapContentFetching(nuxeoController, path);
            PropertyList facets = currentDoc.getFacets();


            returnObject = initContent(request,rootPath, currentDoc, true, null);

            // Get hierarchy
            
            List<Map<String, Object>> hierarchy = new ArrayList<>();
            String hierarchyPath = currentDoc.getPath().substring(0, currentDoc.getPath().lastIndexOf('/'));

            // 1/ Get the list of parent path
            List<String> hierarchyPathToLoad = new ArrayList<>();
            while (hierarchyPath.contains(rootPath)) {
                hierarchyPathToLoad.add(hierarchyPath);
                
                // next parent
                hierarchyPath = hierarchyPath.substring(0, hierarchyPath.lastIndexOf('/'));
            }
            
            // 2/ Get the hierarchy docs
            @SuppressWarnings("unchecked")
            Map<String, Document> hierarchyDocs = (Map<String, Document>) nuxeoController.executeNuxeoCommand(new GetByPathCommand(hierarchyPathToLoad));
            
            
            // 3/ Build hierarchy 
            hierarchyPath = currentDoc.getPath().substring(0, currentDoc.getPath().lastIndexOf('/'));
            
            while (hierarchyPath.contains(rootPath)) {
                Document hierarchyDoc = hierarchyDocs.get(hierarchyPath);
                hierarchy.add(0, initContent(request, rootPath, hierarchyDoc));

                // next parent
                hierarchyPath = hierarchyPath.substring(0, hierarchyPath.lastIndexOf('/'));
            }

            returnObject.put("parents", hierarchy);


            List<Map<String, Object>> childrenList = new ArrayList<>();

            if (facets.list().contains("Folderish")) {

                // Add childrens
                Documents children = (Documents) nuxeoController.executeNuxeoCommand(new FolderGetChildrenCommand(currentDoc));

                for (Document doc : children.list()) {
                    childrenList.add(initContent(request, rootPath, doc));
                }

            }
            returnObject.put("childrens", childrenList);

        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(ctx, e);
        }
        return returnObject;

    }

    
    
    
    /**
     * Download file
     * 
     * @param request
     * @param response
     * @param id
     * @param principal
     * @return
     * @throws Exception
     */

    @SuppressWarnings("deprecation")
    @RequestMapping(value = "/Drive.download", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)

    public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id", required = true) String id,
            Principal principal) throws Exception {
        WSPortalControllerContext ctx = new WSPortalControllerContext(request, response, principal);
        NuxeoController nuxeoController = getNuxeocontroller(request, principal);
        
        ServletOutputStream output = response.getOutputStream();

     
        try {
            nuxeoController.setStreamingSupport(false);
            String path = IWebIdService.FETCH_PATH_PREFIX + id;

            CMSBinaryContent content = nuxeoController.fetchFileContent(path, "file:content");

            response.setContentType(content.getMimeType());
            response.setHeader("Content-Disposition", "attachment; "+"filename=\"" + content.getName() + "\"");
            response.setHeader("Content-Length", String.valueOf(content.getFileSize()));

            ResourceUtil.copy(new FileInputStream(content.getFile()), response.getOutputStream(), 4096);            
          
        }  catch (NuxeoException e) {
            if (e.getErrorCode() == NuxeoException.ERROR_NOTFOUND) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (e.getErrorCode() == NuxeoException.ERROR_FORBIDDEN) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else if (e.getErrorCode() == NuxeoException.ERROR_UNAVAILAIBLE) {
                throw new ServletException(e);
            }
        } catch (Exception e) {
            String token = errorMgr.logError(ctx, e);      

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, token);   
        } finally {
            IOUtils.closeQuietly(output);
        }

    }

    
    
   
    
    
    /**
     * Get content datas for the specified id.
     *
     * @param request the request
     * @param response the response
     * @param fullPathByTitle the full path by title
     * @param type the type
     * @param mimeType the mime type
     * @param principal the principal
     * @return the map
     * @throws Exception the exception
     */

    @RequestMapping(value = "/Drive.search", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)

    public Map<String, Object> search(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "path", required = false) String fullPathByTitle, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "mimeType", required = false) String mimeType, 
            Principal principal) throws Exception {

        
        WSPortalControllerContext ctx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        try {
            NuxeoController nuxeoController = getNuxeocontroller(request, principal);
            
            // Get root
            String rootPath = getRootPath(nuxeoController, principal);
            Document rootDoc = wrapContentFetching(nuxeoController, rootPath);
            
            
            Document baseDoc;
            String baseTitlePath ;
            
            
            // Normalize search string 
            if( StringUtils.isNotEmpty(fullPathByTitle))    {
                
                if( !fullPathByTitle.startsWith(PATH_SEPARATOR))
                    return errorMgr.getErrorResponse(ERR_SEARCH_INCORRECT_PATH, "The path is not correct");
                
                // remove last slash               
                if( fullPathByTitle.endsWith(PATH_SEPARATOR))
                    fullPathByTitle = fullPathByTitle.substring(0, fullPathByTitle.length() -1 );
            }

            
            if( StringUtils.isNotEmpty(fullPathByTitle)) {   
                baseDoc = prepareFolder(nuxeoController,  rootDoc, fullPathByTitle, false);
                baseTitlePath = fullPathByTitle;
            }
            else    {
                baseDoc = rootDoc;
                baseTitlePath = "/";
            }
  
            // control the type
            if( StringUtils.isNotEmpty(type))   {
                if( (! DriveRestController.DRIVE_TYPE_FOLDER.equals(type)) && (! DriveRestController.DRIVE_TYPE_FILE.equals(type)))  {          
                     return errorMgr.getErrorResponse(ERR_SEARCH_INCORRECT_TYPE, "This type is not supported");                
                    }
            }
            
    
            List<Map<String, Object>> childrenList = new ArrayList<>();

            // Get full hierarchy
            Map<String, Document> hierarchyDocs = (Map<String, Document>) nuxeoController.executeNuxeoCommand(new GetHierarchyCommand(baseDoc.getPath()));
            
            // Get search results
            Map<String, Document> children = (Map<String, Document>) nuxeoController.executeNuxeoCommand(new SearchCommand(baseDoc.getPath(), type, mimeType));           

            for (String path : children.keySet()) {

                String childTitlePath = getFullTitle(baseDoc.getPath(), hierarchyDocs, path);
                
                Document doc = children.get(path);
                
                // Add base title
                if( childTitlePath.length() > 0) {
                    if( !baseTitlePath.endsWith(PATH_SEPARATOR) )
                        childTitlePath = PATH_SEPARATOR + childTitlePath;
                }
                
                childTitlePath = baseTitlePath +  childTitlePath;
                
                childrenList.add(initContent(request, rootPath, doc, false, childTitlePath));
            }


            returnObject.put("items", childrenList);

        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(ctx, e);
        }

        
        
        return returnObject;

    }


    /**
     * Gets the full title (including hierarchy)
     *
     * @param basePath the base path
     * @param hierarchyDocs the hierarchy docs
     * @param path the path
     * @return the full title
     */
    
    private String getFullTitle(String basePath, Map<String, Document> hierarchyDocs, String path) {
        
        String childTitlePath = "";
        String remainingpath = path;
        
          
        while (remainingpath.length() > basePath.length()) {
            // Search parents
            Document hierarchyDoc = hierarchyDocs.get(remainingpath);
            if( hierarchyDoc == null) {
                   throw new RuntimeException("Inconsistent hierarchy");
            }
            
            if( childTitlePath.length() > 0)    
                childTitlePath = PATH_SEPARATOR + childTitlePath;
            
            childTitlePath = hierarchyDoc.getTitle() + childTitlePath;
            
            int lastSlash = remainingpath.lastIndexOf(PATH_SEPARATOR);
            if( lastSlash == -1) {
                throw new RuntimeException("Inconsistent hierarchy");
            }
            remainingpath = remainingpath.substring(0, lastSlash);
        }
        return childTitlePath;
    }
    
    

    /**
     * Gets the root path.
     *
     * @param nuxeoController the nuxeo controller
     * @param principal the principal
     * @return the root path
     */
    private String getRootPath(NuxeoController nuxeoController, Principal principal) {
        HttpServletRequest request = nuxeoController.getServletRequest();
        HttpSession session = request.getSession(false);
        
        String rootPath = (String) session.getAttribute("osivia.rootPath");
        if( rootPath == null)   {
            Document userWorkspace = (Document) nuxeoController.executeNuxeoCommand(new GetUserProfileCommand(principal.getName()));
            rootPath = userWorkspace.getPath().substring(0, userWorkspace.getPath().lastIndexOf('/')) + "/documents";
            session.setAttribute("osivia.rootPath", rootPath);
        }
        return rootPath;
    }

    
    


    /**
     * Get folder by title on several levels (and creates it if necessary)
     *
     * @param nuxeoController the nuxeo controller
     * @param parentDoc the parent doc
     * @param titleFullPath the title full path
     * @param createIfNeeded the create if needed
     * @return the document
     * @throws NotFoundException the not found exception
     */
    private Document prepareFolder(NuxeoController nuxeoController, Document parentDoc, String titleFullPath, boolean createIfNeeded) throws NotFoundException   {
        return prepareRecursiveFolder( nuxeoController, parentDoc,  titleFullPath,   createIfNeeded, titleFullPath) ;
    }
    

    
    /**
     * Prepare folder by title (and creates it if necessary)
     *
     * @param nuxeoController the nuxeo controller
     * @param parentDoc the parent doc
     * @param titleFullPath the title full path
     * @param createIfNeeded the create if needed
     * @param remainingPath the remaining path
     * @return the document
     * @throws NotFoundException the not found exception
     */
    private Document prepareRecursiveFolder(NuxeoController nuxeoController, Document parentDoc, String titleFullPath,   boolean createIfNeeded, String remainingPath) throws NotFoundException   {

        if( !remainingPath.startsWith(FOLDER_SEPARATOR))
            throw new IllegalArgumentException("path must start with '"+FOLDER_SEPARATOR+"'");
        
        // Remove first slash
        remainingPath = remainingPath.substring(1);
        
        // Extract current tile
        String currentTitle;
        int idxTitle = remainingPath.indexOf(FOLDER_SEPARATOR);
        if( idxTitle != -1)
            currentTitle = remainingPath.substring(0, idxTitle);
        else
            currentTitle = remainingPath;
        
        // Recompute remaining path
        remainingPath = remainingPath.substring(currentTitle.length());
        
        INuxeoCommand checkCmd = new FetchByTitleCommand(parentDoc, currentTitle);
        Documents docs = (Documents) nuxeoController.executeNuxeoCommand(checkCmd);
        if (docs.size() == 0) {
            if( createIfNeeded) {
                // Create subfolder
                INuxeoCommand command = new CreateFolderCommand(parentDoc, currentTitle);
                parentDoc = (Document) nuxeoController.executeNuxeoCommand(command);   
            }   else    {
                throw new NotFoundException("titleFullPath");
            }
        } else 
            parentDoc = docs.get(0);
        
        if( remainingPath.length() > 0 )
            return prepareRecursiveFolder(nuxeoController,  parentDoc, titleFullPath, createIfNeeded,remainingPath );
        else
            return parentDoc;
    }
    
    /**
     * Upload a file to the current folder
     * 
     * @param file
     * @param parentWebId
     * @param extraField
     * @param request
     * @param response
     * @throws Exception
     */

    @RequestMapping(value = "/Drive.upload", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleFileUpload(@RequestParam(DRIVE_TYPE_FILE) MultipartFile file, @RequestParam("uploadInfos") String fileUpload,
            HttpServletRequest request, HttpServletResponse response, Principal principal) throws Exception {


        WSPortalControllerContext ctx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        try {
           NuxeoController nuxeoController = getNuxeocontroller(request, principal);

            UploadBean uploadBean = new ObjectMapper().readValue(fileUpload, UploadBean.class);

            /* Get folder (creates if none) */

            Document parentDoc;

            if (StringUtils.isNotEmpty(uploadBean.getParentPath())) {
                // Check name
                if (!checkFileName(uploadBean.getParentPath().replaceAll(FOLDER_SEPARATOR, "")) )
                    return errorMgr.getErrorResponse(ERR_UPLOAD_INVALID_CHARACTER, "Parent path contains invalid characters");
                
                // Check parent hierarchy
                String rootPath = getRootPath(nuxeoController, principal);
                Document rootDoc = wrapContentFetching(nuxeoController, rootPath);
                parentDoc = prepareFolder(nuxeoController, rootDoc, uploadBean.getParentPath(), true);

            } else {
                // Get parent doc
                parentDoc = wrapContentFetching(nuxeoController, IWebIdService.FETCH_PATH_PREFIX + uploadBean.getParentId());
            }

            /* Get the OAuth2 client ID */

            Authentication a = SecurityContextHolder.getContext().getAuthentication();
            String clientId = ((OAuth2Authentication) a).getOAuth2Request().getClientId();


            if (!StringUtils.isEmpty(uploadBean.getIfFileExists()) && !StringUtils.equalsIgnoreCase(UploadBean.ACTION_IGNORE, uploadBean.getIfFileExists())
                    && !StringUtils.equalsIgnoreCase(UploadBean.ACTION_OVERWRITE, uploadBean.getIfFileExists())
                    && !StringUtils.equalsIgnoreCase(UploadBean.ACTION_RENAME, uploadBean.getIfFileExists()))
                return errorMgr.getErrorResponse(ERR_UPLOAD_INVALID_ACTION, "parameter onFileExists is incorrect :" + uploadBean.getIfFileExists());


            boolean doUpload = true;
             
            // Check file name
            if (!checkFileName(file.getOriginalFilename()) )
                return errorMgr.getErrorResponse(ERR_UPLOAD_INVALID_CHARACTER, "File name contains invalid characters");            

            if (StringUtils.isEmpty(uploadBean.getIfFileExists()) || StringUtils.equalsIgnoreCase(UploadBean.ACTION_IGNORE, uploadBean.getIfFileExists())) {
                // If file exists and ignore, do nothing
                INuxeoCommand checkCmd = new FetchByTitleCommand(parentDoc, file.getOriginalFilename());
                Documents docs = (Documents) nuxeoController.executeNuxeoCommand(checkCmd);
                if (docs.size() > 0) {
                    doUpload = false;
                }
            }

            if (doUpload) {
                // Execute import
                boolean overwrite = StringUtils.equalsIgnoreCase(UploadBean.ACTION_OVERWRITE, uploadBean.getIfFileExists());
                INuxeoCommand command = new UploadFileCommand(parentDoc.getId(), file, overwrite);
                Document doc = (Document) nuxeoController.executeNuxeoCommand(command);
                


                // set qualifiers
                // retrieve doc webId
                doc = wrapContentFetching(nuxeoController, doc.getPath());
                UpdatedProperties properties = parseProperties(ctx, doc.getProperties().getString(PROP_TTC_WEBID), clientId, uploadBean.getProperties());
                INuxeoCommand updateCommand = new AddPropertiesCommand(doc, properties);
                nuxeoController.executeNuxeoCommand(updateCommand);
                
                returnObject.put("contentId", doc.getProperties().getString(PROP_TTC_WEBID));
            }


        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(ctx, e);
        }


        return returnObject;
    }

    
    
    
    
    

    /**
     * Check file name.
     *
     * @param fileName the file name
     * @return true, if successful
     */
    private boolean checkFileName(String fileName) {
        // illegal characters : /\:*?<>!
         Pattern pattern = Pattern.compile(FILE_NAME_PATTERN);
        if (!pattern.matcher(fileName).matches()) {
            return false;
        }
        
        return true;
    }


    /**
     * Create a folder
     * 
     * @param file
     * @param parentWebId
     * @param extraField
     * @param request
     * @param response
     * @throws Exception
     */

    @RequestMapping(value = "/Drive.createFolder", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> createFolder(@RequestBody CreateFolderBean createFolderBean, HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {

        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);
        try {

            NuxeoController nuxeoController = getNuxeocontroller(request, principal);


            String path = IWebIdService.FETCH_PATH_PREFIX + createFolderBean.getParentId();

            Document parentDoc = wrapContentFetching(nuxeoController, path);


            if (StringUtils.isEmpty(createFolderBean.getFolderName()))
                returnObject = errorMgr.getErrorResponse(ERR_CREATE_FOLDER_EMPTY_NAME, "The folder name is empty");
            else {
                // Check if already exist
                INuxeoCommand checkCmd = new FetchByTitleCommand(parentDoc, createFolderBean.getFolderName());
                Documents docs = (Documents) nuxeoController.executeNuxeoCommand(checkCmd);

                if (docs.size() > 0) {
                    returnObject = errorMgr.getErrorResponse(ERR_CREATE_FOLDER_ALREADY_EXISTS, "This folder already exists");
                } else {

                    // Execute creation
                    INuxeoCommand command = new CreateFolderCommand(parentDoc, createFolderBean.getFolderName());

                    nuxeoController.executeNuxeoCommand(command);

                    // we must fetch the document to get the webId (not present in the returned document)
                    INuxeoCommand loadCmd = new FetchByTitleCommand(parentDoc, createFolderBean.getFolderName());
                    Documents createdDocs = (Documents) nuxeoController.executeNuxeoCommand(loadCmd);
                    if (createdDocs.size() == 1) {
                        returnObject.put("folderId", createdDocs.get(0).getProperties().getString(PROP_TTC_WEBID));
                    } else
                        throw new Exception("can't fetch created folder. createdDocs.size = " + createdDocs.size());


                }
            }


        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }


        return returnObject;
    }


    /**
     * Publish a document to the specified target
     * 
     * @param file
     * @param parentWebId
     * @param extraField
     * @param request
     * @param response
     * @throws Exception
     */

    @RequestMapping(value = "/Drive.getShareUrl", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getSharedUrl(@RequestBody GetSharedUrlBean sharedUrlBean, HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {

        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);
        try {

            NuxeoController nuxeoController = getNuxeocontroller(request, principal);


            String path = IWebIdService.FETCH_PATH_PREFIX + sharedUrlBean.getContentId();
            NuxeoDocumentContext ctx = nuxeoController.getDocumentContext(path);

            Document currentDoc = wrapContentFetching(nuxeoController, path);


            // Check format
            boolean checkFormat = false;
            if (StringUtils.isNotEmpty(sharedUrlBean.getFormat())) {
                if (DEFAULT_FORMAT.equals(sharedUrlBean.getFormat())) {
                    checkFormat = true;
                } else if (NATIVE_FORMAT.equals(sharedUrlBean.getFormat())) {
                    checkFormat = true;
                } else if (PDF_FORMAT.equals(sharedUrlBean.getFormat())) {
                    if (ispdfConvertible(currentDoc))
                        checkFormat = true;
                }
            } else {
                checkFormat = true;
            }

            if (!checkFormat) {
                returnObject = errorMgr.getErrorResponse(1, "Format not supported");
            } else {

                // Execute publish
                INuxeoCommand command = new GetSharedUrlCommand(currentDoc, sharedUrlBean.getFormat(), sharedUrlBean.isPublish());

                @SuppressWarnings("unchecked")
                Map<String, String> returnMap = (Map<String, String>) nuxeoController.executeNuxeoCommand(command);

                // Prepare results
                returnObject.put("shareUrl", getUrl(request) + SHARE_URL_PREFIX + returnMap.get("shareId"));
                returnMap.remove("shareId");


                // Force cache initialisation
                ctx.reload();
            }

        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }


        return returnObject;
    }


    /**
     * Publish a document to the specified target
     * 
     * @param file
     * @param parentWebId
     * @param extraField
     * @param request
     * @param response
     * @throws Exception
     */

    @RequestMapping(value = "/Drive.publish", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> publish(@RequestBody PublishBean publishBean, HttpServletRequest request, HttpServletResponse response, Principal principal)
            throws Exception {

        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);
        try {

            NuxeoController nuxeoController = getNuxeocontroller(request, principal);


            // Get the OAuth2 client ID
            Authentication a = SecurityContextHolder.getContext().getAuthentication();
            String clientId = ((OAuth2Authentication) a).getOAuth2Request().getClientId();


            Document currentDoc = null;

            // Extract share ID
            String url = publishBean.getShareUrl();
            int iName = url.lastIndexOf('/');
            if (iName != -1) {
                currentDoc = nuxeoController.fetchSharedDocument(url.substring(iName + 1), false);
            }


            // Execute publish
            INuxeoCommand command = new PublishCommand(currentDoc, publishBean, clientId);
            

            @SuppressWarnings("unchecked")
            Map<String, String> returnMap = (Map<String, String>) nuxeoController.executeNuxeoCommand(command);
            
            // set qualifiers
            UpdatedProperties properties = parseProperties(wsCtx, currentDoc.getProperties().getString(PROP_TTC_WEBID), clientId,
                    publishBean.getProperties());
            INuxeoCommand updateCommand = new AddPropertiesCommand(currentDoc, properties);
            nuxeoController.executeNuxeoCommand(updateCommand);



            // Prepare results
            returnObject.put("pubId", returnMap.get("pubId"));

            // Force cache initialisation
            NuxeoDocumentContext ctx = nuxeoController.getDocumentContext(currentDoc.getPath());
            ctx.reload();
            

        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }


        return returnObject;
    }


    /**
     * Publish a document to the specified target
     * 
     * @param file
     * @param parentWebId
     * @param extraField
     * @param request
     * @param response
     * @throws Exception
     */

    @RequestMapping(value = "/Drive.unpublish", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> unpublish(@RequestBody UnpublishBean unpublishBean, HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {

        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        try {

            NuxeoController nuxeoController = getNuxeocontroller(request, principal);

            // Find doc by pubId
            Documents docs = (Documents) nuxeoController.executeNuxeoCommand(new FetchByPubIdCommand(unpublishBean.getPubId()));

            if (docs.size() > 0) {

                if (docs.size() > 1)
                    throw new Exception("more than one pubId detected");

                Document currentDoc = docs.get(0);
                NuxeoDocumentContext ctx = nuxeoController.getDocumentContext(currentDoc.getPath());

                // unpublish
                nuxeoController.executeNuxeoCommand(new UnpublishCommand(currentDoc, unpublishBean.getPubId()));

                // Force cache initialisation
                ctx.reload();

            }

            else {
                returnObject = errorMgr.getErrorResponse(1, "Publication with id '" + unpublishBean.getPubId() + "' not found");
            }


        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }


        return returnObject;
    }


    @RequestMapping(value = "/Admin.createUser", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> createUser(@RequestBody CreateUserBean userBean, HttpServletRequest request, HttpServletResponse response, Principal principal)
            throws Exception {


        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        try {

            Person findPerson = personUpdateService.getEmptyPerson();
            findPerson.setMail(userBean.getMail());
            if (personUpdateService.findByCriteria(findPerson).size() > 0) {
                returnObject.put("returnCode", ERR_CREATE_USER_MAIL_ALREADYEXIST);
                return returnObject;
            }

            // Person
            Person person = personUpdateService.getEmptyPerson();
            person.setUid(userBean.getMail());
            person.setCn(userBean.getMail());
            person.setSn(userBean.getFirstName() + " " + userBean.getLastName());
            person.setGivenName(userBean.getFirstName());
            person.setDisplayName(userBean.getFirstName() + " " + userBean.getLastName());
            person.setMail(userBean.getMail());

            personUpdateService.create(person);
            personUpdateService.updatePassword(person, "osivia");


        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }
        return returnObject;
    }
    

    
    @RequestMapping(value = "/Admin.getStorage", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> storage(HttpServletRequest request, @RequestParam(value = "pageSize", required = false) String sPageSize, @RequestParam(value = "pageNumber", required = false) String sPageNumber ,HttpServletResponse response, Principal principal)
            throws Exception {
        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);
        
        
        NuxeoController nuxeoController = getNuxeocontroller(request, principal);
        

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);   
        

        
        try {
            // Page number
            int pageNumber = 0;
            if( sPageNumber != null)
                pageNumber = Integer.parseInt(sPageNumber);  
           
            // Page size
            int pageSize = 100;
            if( sPageSize != null)
                pageSize = Integer.parseInt(sPageSize);
  
            // Get persons from LDAP
            Person findPerson = personUpdateService.getEmptyPerson();
            List<Person> personsList = personUpdateService.findByCriteria(findPerson);
            
            
            // Filter by page
            List<Person> filteredList = new ArrayList<Person>();
            for(int i=pageNumber*pageSize; i< (pageNumber+1)*pageSize ;i++) {
                if( i < personsList.size())
                    filteredList.add(personsList.get(i));
            }
           
            // Display variables
            returnObject.put("pageNumber", pageNumber);
            returnObject.put("pageSize", pageSize); 
            returnObject.put("totalUsersCount", personsList.size()); 
            returnObject.put("pageUsersCount", filteredList.size());   
            
            List<UserStorageBean> userStorages = new ArrayList<>();

            // Person
            for(Person person: filteredList)   {
                
                UserStorageBean userStorage = new UserStorageBean();
                userStorage.setUserId(person.getUid());
                
                Document userWorkspace = (Document) nuxeoController.executeNuxeoCommand(new GetUserProfileCommand(person.getUid()));
                if (userWorkspace != null) {

                    PropertyList tokens = userWorkspace.getProperties().getList("oatk:tokens");
                    for (int i = 0; i < tokens.size(); i++) {
                        PropertyMap tokenMap = tokens.getMap(i);
                        String jsonData = tokenMap.getString("authentication");
                        if (jsonData != null) {
                            PortalRefreshTokenAuthenticationDatas datas = new ObjectMapper().readValue(jsonData, PortalRefreshTokenAuthenticationDatas.class);
                            String clientId = datas.getClientId();
                            if( StringUtils.isNotEmpty(clientId))   {
                                userStorage.getClientId().add(datas.getClientId());
                            }
                        }
                    }
                    
                    String rootPath = userWorkspace.getPath().substring(0, userWorkspace.getPath().lastIndexOf('/'));
                    INuxeoCommand command = new GetQuotaCommand(rootPath);

                    long fileSize = 0;
                    long quota = 0;
                    long number = 0;
                   
                    
                    Blob quotaInfos =  (Blob) nuxeoController.executeNuxeoCommand(command);        
                    
                    if (quotaInfos != null) {
                        
                        String quotaInfosContent;
                        try {
                            quotaInfosContent = IOUtils.toString(quotaInfos.getStream(), "UTF-8");
                        } catch (IOException e) {
                            throw new PortletException( e);
                        }

                        JSONObject quotaContent = JSONObject.fromObject(quotaInfosContent);
                        
                        fileSize = quotaContent.getLong("treesize");
                        quota = quotaContent.getLong("quota");
                        number = quotaContent.getLong("number");                        
                     }
                    
                    userStorage.setFileSize(fileSize);
                    userStorage.setQuota(quota);
                    userStorage.setNumber(number);
                }
                

                

                userStorages.add(userStorage);
                

            }
            returnObject.put("userStorages", userStorages);
            
        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }
        
        return returnObject;
    }
    
    @RequestMapping(value = "/Admin.supervise", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> supervisor(HttpServletRequest request, HttpServletResponse response, Principal principal)
            throws Exception {


        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        try {

            // Test shared space
            try {
                String exportPath = (String) System.getProperties().get("exportaccount.path");

                File testFile = File.createTempFile("supervisor-", ".tmp");
                FileOutputStream outputStream = new FileOutputStream(testFile);
                byte[] strToBytes = "this is juste a supervising test. Don't worry".getBytes();
                outputStream.write(strToBytes);
                outputStream.close();

                String targetName = exportPath + "supervisor.test";

                // Create and move a file
                File existingFile = new File(targetName);
                if (new File(targetName).exists())
                    existingFile.delete();

                Files.move(Paths.get(testFile.getAbsolutePath()), Paths.get(targetName));
                
                // Delete the file
                new File(targetName).delete();

                returnObject.put("exports.write", "ok");
            } catch (Exception e) {
                returnObject.put("exports.write", "KO - " + e.getMessage());
            }
            
            // Test nuxeo + binaries
            try {
                Document configuration = conversionRepository.getConfigurationDocument(false);
                 
                PropertyMap map = configuration.getProperties().getMap(ConversionAdminService.XPATH);
                 if( map == null)
                    throw new Exception("No conversion file found");
                returnObject.put("nuxeo.binaries", "ok");
            } catch (Exception e) {
                returnObject.put("nuxeo.binaries", "KO - " + e.getMessage());
            } 
            
            
            // Test ldap
            if(principal != null && StringUtils.isNotEmpty(principal.getName())) {
                try {
                    Person findPerson = personUpdateService.getEmptyPerson();
                    findPerson.setUid(principal.getName());
                    if (personUpdateService.findByCriteria(findPerson).size() == 1) {
                        returnObject.put("nuxeo.ldap", "ok");
                    } else
                        throw new Exception("No user with uid=" + principal.getName());
                } catch (Exception e) {
                    returnObject.put("nuxeo.ldap", "KO - " + e.getMessage());
                    
                } 
            }
           
            // Test ssh
            try {
                File f = new File("/home/portal/supervise.xml");
                if (f.exists()) {
                    FileInputStream fis = new FileInputStream(f);
                    try {
                        returnObject.put("env",  new GlobalChecker().run(fis));
                     } finally {
                        fis.close();
                    }
                }
            }
            
            catch (Exception e) {
                returnObject.put("ssh", "KO - " + e.getMessage());
            }            
           

        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }
        return returnObject;
    }


    @RequestMapping(value = "/Drive.error", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> error(HttpServletRequest request, HttpServletResponse response, Principal principal,
            @RequestParam(value = "type", required = false) String type) throws Exception {

        WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response, principal);

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        try {
            if ("exception".equals(type))
                throw new Exception("this is the exception msg");

            if ("error".equals(type))
                returnObject = errorMgr.getErrorResponse(3, "Erreur de test");

        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }
        return returnObject;
    }


    private UpdatedProperties parseProperties(PortalControllerContext ctx, String docId, String clientId, DocumentProperties requestProperties) {
        // set qualifiers
        UpdatedProperties updatedProperties = new UpdatedProperties();
        
        if(  requestProperties.getLevels() != null) {
            List<String> levels = new ArrayList<>();
            for(MetadataClassifier level : requestProperties.getLevels())   {
                String standardLevel = convertLevelQualifier(ctx, docId, clientId, level);
                if (standardLevel != null)  {
                    levels.add(standardLevel);
                }
            }
            if( levels.size() > 0)
                updatedProperties.setLevels(levels);            
        }

        if(  requestProperties.getSubject() != null)    {
            String standardSubject = convertSubjectQualifier(ctx, docId, clientId, requestProperties.getSubject());
            if (standardSubject != null)
                updatedProperties.setSubject(standardSubject);
        }
        
        String documentType = requestProperties.getDocumentType();
        if( StringUtils.isNotEmpty(documentType))
            updatedProperties.setDocumentType(documentType); 
        
        if(  requestProperties.getKeywords() != null) {
            if( requestProperties.getKeywords().size() > 0)
                updatedProperties.setKeywords(requestProperties.getKeywords());
        }
        
        return updatedProperties;
    }

    /**
     * Convert the local qualifier to the standard one
     * 
     * @param pronoteQualifier
     * @return the supported qualifier, or null
     */
    private String convertLevelQualifier(PortalControllerContext ctx, String docId, String clientId, MetadataClassifier level) {
        return conversionService.convert(ctx, docId, clientId, "L", level);
    }

    /**
     * Convert the local qualifier to the standard one
     * 
     * @param pronoteQualifier
     * @return the supported qualifier, or null
     */
    private String convertSubjectQualifier(PortalControllerContext ctx, String docId, String clientId,  MetadataClassifier subject) {
        return conversionService.convert(ctx, docId, clientId, "S", subject);
    }

    /**
     * @return
     */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver createMultipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        return resolver;
    }


}
