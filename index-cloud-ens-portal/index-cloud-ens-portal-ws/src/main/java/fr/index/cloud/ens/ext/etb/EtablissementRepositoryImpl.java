package fr.index.cloud.ens.ext.etb;

import org.nuxeo.ecm.automation.client.model.Document;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.core.web.IWebIdService;
import org.springframework.stereotype.Repository;

import fr.index.cloud.ens.application.api.Application;
import fr.index.cloud.ens.ws.DriveRestController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoException;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

/**
 * The Class EtablissementRepositoryImpl.
 */


@Repository
public class EtablissementRepositoryImpl implements EtablissementRepository {

    public static String APPLICATION_FOLDER_NAME = "applications";

    public static String APPLICATION_TYPE = "OAuth2Application";  
    
    /** Title Nuxeo document property. */
    public static String TITLE_PROPERTY = "dc:title";
    
    /**
     * Gets the nuxeo controller.
     *
     * @return the nuxeo controller
     */
    private NuxeoController getNuxeoController() {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT);
        return nuxeoController;
    }



    /**
     * Gets the nuxeo controller.
     *
     * @return the nuxeo controller
     */
    private NuxeoController getNuxeoControllerNoCache() {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }
  
    
    /**
     * Gets the root path.
     *
     * @return the root path
     */
    public static String getRootPath() {
        // Check if root folder exists
        String folderPath = System.getProperty("config.rootPath") + "/" + APPLICATION_FOLDER_NAME;

        return folderPath;
    }
    
    

    /**
     * Gets the root.
     *
     * @return the root
     */
    private Document getRoot() {
        Document root;
        try {
            root = getNuxeoController().getDocumentContext(getRootPath()).getDocument();
        } catch (NuxeoException e) {
            if (e.getErrorCode() == NuxeoException.ERROR_NOTFOUND)
                root = (Document) getNuxeoController().executeNuxeoCommand(new ApplicationCreateRootCommand());
            else
                throw e;
        }

        return root;

    }

    @Override
    public void update(Application application) {

        Document root = getRoot();
        
        NuxeoController nuxeoController = getNuxeoController();
        
        // Check root
        try {
            root = nuxeoController.getDocumentContext(getRootPath()).getDocument();
        } catch (NuxeoException e) {
            if (e.getErrorCode() == NuxeoException.ERROR_NOTFOUND)
                root = (Document) getNuxeoController().executeNuxeoCommand(new ApplicationCreateRootCommand());
            else
                throw e;
        }       
        
        // Check if application exists
        String applicationPath = IWebIdService.FETCH_PATH_PREFIX + application.getCode();
     
 
        Document doc;
        try {
            doc = nuxeoController.getDocumentContext(applicationPath).getDocument();
        } catch (NuxeoException e) {
            if (e.getErrorCode() == NuxeoException.ERROR_NOTFOUND)
                doc = null;
            else
                throw e;
        }       
      
        // Create or update document
        getNuxeoControllerNoCache().executeNuxeoCommand(new ApplicationEditionCommand(root,application, doc));
        
        nuxeoController.getDocumentContext(applicationPath).reload();
        
    }


    @Override
    public Application getApplication(String code) {
        // Check if application exists
        String applicationPath = IWebIdService.FETCH_PATH_PREFIX + code;
     
        Document doc;
        try {
            doc = getNuxeoController().getDocumentContext(applicationPath).getDocument();
        } catch (NuxeoException e) {
            if (e.getErrorCode() == NuxeoException.ERROR_NOTFOUND)
                doc = null;
            else
                throw e;
        }
        
        Application application;
        
        if( doc != null)    {
            application = new Application(code, doc.getTitle());
            String description = doc.getProperties().getString("dc:description");
            application.setDescription(description);
        }
        else
            application = null;
        
        return application;
    }


    @Override
    public void delete(String code) {
        // Check if application exists
        String applicationPath = IWebIdService.FETCH_PATH_PREFIX + code;
        
        Document doc = getNuxeoController().getDocumentContext(applicationPath).getDocument();
        
        // delete
        if( doc != null)
            getNuxeoController().executeNuxeoCommand(new ApplicationDeleteCommand(doc));

        
    }

}
