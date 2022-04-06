package fr.index.cloud.ens.ws.nuxeo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.portlet.PortletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.core.cms.CMSBinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.index.cloud.ens.ws.DriveRestController;
import fr.index.cloud.ens.ws.commands.GetUserProfileCommand;
import fr.index.cloud.oauth.authentication.PortalAuthentication;
import fr.index.cloud.oauth.authentication.PortalAuthenticationProvider;
import fr.index.cloud.oauth.config.CheckHeaderCompatibility;
import fr.index.cloud.oauth.tokenStore.PortalRefreshToken;
import fr.index.cloud.oauth.tokenStore.PortalTokenStore;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;
import fr.toutatice.portail.cms.nuxeo.api.ResourceUtil;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

@RestController
public class NuxeoDrive {
    


    public static PortletContext portletContext;
    
    public final static String NUXEO_DRIVE_SCOPE = "nx-drive-operations";
    public final static String NUXEO_DRIVE_CLIENT_ID = "NXDRIVE";
    
    private Map<String, DriveUploadedFile> uploadedFiles = new ConcurrentHashMap<String, DriveUploadedFile>();
    
    @Autowired
    private ApplicationContext appContext;
    
    @Autowired
    PortalAuthenticationProvider authProvider;
    
    @PostConstruct
    public void initFilter()    {
        CheckHeaderCompatibility.appContext = appContext;
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class NoContentException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public class UnautorizedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }  
    
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public class ForbiddenException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    } 
    
    
    @RequestMapping(value = "/nuxeo/authentication/token", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getAuth( HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {
        

        DefaultTokenServices  tokenServices = appContext.getBean("defaultAuthorizationServerTokenServices", DefaultTokenServices.class);
        PortalAuthenticationProvider  portalAuthenticationProvider = appContext.getBean( PortalAuthenticationProvider.class);


        
        String autorisation = request.getHeader("authorization");
        
        if( !StringUtils.isEmpty(autorisation)) {
            if( autorisation.startsWith("Basic"))   {
           
                autorisation = autorisation.substring(autorisation.lastIndexOf(' ') +1);
                
                byte[] decodedBytes = Base64.getDecoder().decode(autorisation);
                String decodedString = new String(decodedBytes);
                String[] tokens = decodedString.split(":");
                
                Set<String> scopes =  new HashSet<String>();
                scopes.add(NuxeoDrive.NUXEO_DRIVE_SCOPE);
        
                OAuth2Request storedRequest = new OAuth2Request(new HashMap<>(), NUXEO_DRIVE_CLIENT_ID,  new ArrayList(), true, scopes, new HashSet<>(), null, null, null);
    
                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
                Authentication userAuthentication = portalAuthenticationProvider.authenticate(new PortalAuthentication(tokens[0], tokens[1], authorities));
                OAuth2Authentication authentication = new OAuth2Authentication(storedRequest, userAuthentication) ;
                OAuth2AccessToken token = tokenServices.createAccessToken( authentication);
                
                return token.getRefreshToken().getValue();
            }
        }
        
        
        throw new UnautorizedException();
    }
    
    
    
    @RequestMapping(value = "/nuxeo/api/v1/upload/handlers", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getUploadHandler( HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {
        return new HashMap<String, Object>();
    }
    
    @RequestMapping(value = "/nuxeo/site/api/v1/me", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getMe( HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {
        return new HashMap<String, Object>();
    }
    
    @RequestMapping(value = "/nuxeo/api/v1/drive/configuration", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getConf( HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {
        Enumeration headers = request.getHeaderNames();
        Map map = request.getParameterMap();   
        
//        return"{\"oauth2_authorization_endpoint\": \"https://cloud-ens.index-education.local/index-cloud-portal-ens-ws/oauth/authorize\", " +
//                "        \"oauth2_client_id\": \"NXDRIVE\"," + 
//                "        \"oauth2_client_secret\": \"NXDRIVE\"," + 
//                "        \"oauth2_openid_configuration_url\": \"\"," + 
//                "        \"oauth2_scope\": \"DRIVE\"," + 
//                "        \"oauth2_token_endpoint\": \"https://cloud-ens.index-education.local/index-cloud-portal-ens-ws/oauth/token\"  "+ 
//                "        , \"KKKKK\": \"https://cloud-ens.index-education.local/index-cloud-portal-ens-ws/oauth/token\"  "+                 
//                "        }";
        

        
        
        return "{\"nxdrive_home\":\""+"/default-domain/UserWorkspaces/a/d/m/admin"+"\", \"locale2\":\"fr\"}";
    }

    @RequestMapping(value = "/nuxeo/site/automation", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getAutomation( HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {
        File file = ResourceUtils.getFile("classpath:automation.txt");
        String s = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        return s;
    }
    
    @RequestMapping(value = "/nuxeo/runningstatus", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getRunningStatus( HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "Ok";
    }
        
    
    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.GetTopLevelFolder", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String getTopLevelFolder( HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        Object res ;
        
        //Ensure synchronization before displaying top level folder
        //TODO : check before if folder alreadr exists

        Document userWorkspace = (Document) nuxeoController.executeNuxeoCommand(new GetUserProfileCommand(principal.getName()));
        String documentPath = userWorkspace.getPath().substring(0, userWorkspace.getPath().lastIndexOf('/')) + "/documents";
        nuxeoController.executeNuxeoCommand(new SynchronizeCommand(documentPath));
          
        res = nuxeoController.executeNuxeoCommand(new GetTopLevelFolderCommand());
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;
        }
    
    
    
    
    
    
    ///index-cloud-portal-ens-ws/nuxeo/nxbigfile/default/8a88565e-25f5-4864-8482-1a0635fe63cf/blobholder:0/images.png
    //index-cloud-portal-ens-ws/nuxeo/nxbigfile/default/e49ea6ad-6f31-4bf7-819f-cf6444783d79/blobholder:0/facture%20(copie).docx    
    
    @RequestMapping(value = "/nuxeo/nxbigfile/default/{id}/{blob}/{name}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void getBigFile(HttpServletRequest request, HttpServletResponse response,
      @PathVariable("id") String id, Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        nuxeoController.setStreamingSupport(false);
        CMSBinaryContent content = ResourceUtil.getBlobHolderContent(nuxeoController, id, "0");
        
        ResourceUtil.copy(new FileInputStream(content.getFile()), response.getOutputStream(), 4096);

    }
    
    
    @RequestMapping(value = "/nuxeo/api/v1/upload", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String startBatch( HttpServletRequest request, HttpServletResponse response, 
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        return "{\"batchId\": "+System.currentTimeMillis()+"}";
    }
    
    

    
    @RequestMapping(value = "/nuxeo/api/v1/upload/{batchId}/{idx}")
    @ResponseStatus(HttpStatus.OK)
    public void checkUpload( HttpServletRequest request, HttpServletResponse response, 
            Principal principal, @PathVariable("batchId") String batchId, @PathVariable("idx") String idx) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        if( uploadedFiles.get(batchId+"/"+idx) == null)
            throw new NoContentException();
    }
    
    
    @RequestMapping(value = "/nuxeo/api/v1/upload/{batchId}/{idx}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED )
    public String upload( HttpServletRequest request, HttpServletResponse response, 
            Principal principal,  @PathVariable("batchId") String batchId, @PathVariable("idx") String idx) throws Exception {
        
        Enumeration headers = request.getHeaderNames();
        
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);

     // Create path components to save the file
        final String name = URLDecoder.decode(request.getHeader("X-File-Name"),"UTF-8");
        final String contentType = request.getHeader("Content-Type");
         

        File file = File.createTempFile("upload", ".upload");
        OutputStream out = new FileOutputStream(file);
        InputStream filecontent = null;
        int total=0;

        try {

            filecontent = request.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
                total +=read;
            }
            
        } finally {
            if (out != null) {
                
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
            
        }
        uploadedFiles.put(batchId+"/"+idx, new DriveUploadedFile(name, contentType, file));
        
        return "{\"batchId\": "+batchId+", \"fileIdx\": "+idx+", \"uploadType\": \"normal\", \"uploadedSize\": "+total+"}";
        
    }
    


    @RequestMapping(value = "/nuxeo/api/v1/upload/{batchId}/{idx}/execute/NuxeoDrive.CreateFile", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK )
    public String createFile( HttpServletRequest request, HttpServletResponse response, 
            Principal principal,  @PathVariable("batchId") String batchId, @PathVariable("idx") String idx, @RequestBody String requestBody) throws Exception {
        
        
        Enumeration headers = request.getHeaderNames();
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        
        DriveUploadedFile uploadedFile =  uploadedFiles.get(batchId+"/"+idx);
        FileBlob blob = getUploadedBlob(uploadedFile);
        
        NxOperationProxyCommand proxyCommand = new NxOperationProxyCommand("NuxeoDrive.CreateFile", requestBody);
        Map<String,String> parameters = new HashMap<>();
        parameters.put("name", uploadedFile.getName());
        proxyCommand.setParameters(parameters);
        proxyCommand.setOperationInput(blob);
        
        Object res = nuxeoController.executeNuxeoCommand(proxyCommand);
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;

    }

    
    @RequestMapping(value = "/nuxeo/api/v1/upload/{batchId}/{idx}/execute/NuxeoDrive.UpdateFile", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK )
    public String updateFile( HttpServletRequest request, HttpServletResponse response, 
            Principal principal,  @PathVariable("batchId") String batchId, @PathVariable("idx") String idx, @RequestBody String requestBody) throws Exception {
        
        
        Enumeration headers = request.getHeaderNames();
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        
        DriveUploadedFile uploadedFile =  uploadedFiles.get(batchId+"/"+idx);
        FileBlob blob = getUploadedBlob(uploadedFile);
        
        NxOperationProxyCommand proxyCommand = new NxOperationProxyCommand("NuxeoDrive.UpdateFile", requestBody);
        Map<String,String> parameters = new HashMap<>();
        parameters.put("name", uploadedFile.getName());
        proxyCommand.setParameters(parameters);
        proxyCommand.setOperationInput(blob);
        
        Object res = nuxeoController.executeNuxeoCommand(proxyCommand);
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;

    }
    
    private FileBlob getUploadedBlob(DriveUploadedFile uploadedFile) {
        if( uploadedFile == null)
            throw new NoContentException();
        
        // File name
        String name = uploadedFile.getName();
        if( name == null)
            name = uploadedFile.getFile().getName();
        // File content type
        String contentType;
        if (uploadedFile.getContentType() == null) {
            contentType = null;
        } else {
            contentType = uploadedFile.getContentType();
        }
        
        FileBlob blob = new FileBlob(uploadedFile.getFile(), name, contentType);
        return blob;
    }
    
    

    
    /* Proxy calls */
    

    @RequestMapping(value = "/nuxeo/json/cmis", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public Map<String, Object> cmis( HttpServletRequest request, HttpServletResponse response,
            Principal principal) throws Exception {
        return new HashMap<String, Object>();
        /*
        String s = new NxHttpProxy("/json/cmis").execute();
        return s;
        */
        }

    
    
    
    
    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.GetChildren", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String getChildren( HttpServletRequest request, HttpServletResponse response, @RequestBody String name,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("NuxeoDrive.GetChildren",name));

        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;
        }

    @RequestMapping(value = "/nuxeo/site/automation/Document.Fetch", method = RequestMethod.POST, produces = "application/json+nxautomation" )
    @ResponseStatus(HttpStatus.OK)
    public String fetch( HttpServletRequest request, HttpServletResponse response, @RequestBody String name,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
  

        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("Document.Fetch", request, name));
        response.setContentType("application/json+nxautomation");
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;
        }

    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.CreateFolder", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String createFolder( HttpServletRequest request, HttpServletResponse response, @RequestBody String name,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        

        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("NuxeoDrive.CreateFolder",name));
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;
        }
    
    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.Delete", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void delete( HttpServletRequest request, HttpServletResponse response, @RequestBody String name,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);

// Do not perform undelete on client side
//        if( true)
//            throw new ForbiddenException();
        
        nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("NuxeoDrive.Delete",name));
        
        }
    
    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.Rename", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String rename( HttpServletRequest request, HttpServletResponse response, @RequestBody String name,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("NuxeoDrive.Rename",name));
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;
        }    
    
    @RequestMapping(value = "/nuxeo/site/automation/Document.Move", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void moveDocument( HttpServletRequest request, HttpServletResponse response, @RequestBody String name,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);

        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("Document.Move",name));
        

        } 
    
    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.Move", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String move( HttpServletRequest request, HttpServletResponse response, @RequestBody String name,
            Principal principal) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        

        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("NuxeoDrive.Move",name));
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;
        }        
    
    
    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.GetFileSystemItem", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String getFileSystemItem( HttpServletRequest request, HttpServletResponse response,
            Principal principal, @RequestBody String fileSystemBody) throws Exception {
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        
        
        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("NuxeoDrive.GetFileSystemItem",fileSystemBody));
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        return s;
        }
    
    @RequestMapping(value = "/nuxeo/site/automation/NuxeoDrive.GetChangeSummary", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String getChangeSummary( HttpServletRequest request, HttpServletResponse response,
            Principal principal, @RequestBody String fileSystemBody) throws Exception {
        
        
        NuxeoController nuxeoController = DriveRestController.getNuxeocontroller(request, principal);
        

        
        Object res = nuxeoController.executeNuxeoCommand(new NxOperationProxyCommand("NuxeoDrive.GetChangeSummary",fileSystemBody));
        
        FileBlob file = (FileBlob) res;
        
        String s = new String(Files.readAllBytes(Paths.get(file.getFile().getAbsolutePath())));
        
       // System.out.println( s);
        return s;
        }

}
