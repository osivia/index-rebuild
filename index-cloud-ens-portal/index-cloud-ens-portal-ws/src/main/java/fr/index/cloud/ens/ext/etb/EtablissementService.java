package fr.index.cloud.ens.ext.etb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.portlet.PortletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.api.status.IStatusService;
import org.osivia.portal.api.status.UnavailableServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import fr.index.cloud.ens.application.api.Application;
import fr.index.cloud.ens.application.api.IApplicationService;
import fr.index.cloud.ens.ws.DriveRestController;

/**
 * Service appelant et mémorisant les étalissements PRONOTE dans des applications OAuth2
 * 
 * @author Jean-Sébastien
 */


@Service
public class EtablissementService implements IApplicationService {

    public static String APPLICATION_ID_PREFIX = "oauth2app_";
    public static final String PRONOTE_CLIENT_PREFIX = "PRONOTE-";

    @Autowired
    ICacheService cacheService;

    @Autowired
    IStatusService statusService;
    
    @Autowired
    IProfilerService profilerService;

    @Autowired
    EtablissementRepository repository;

    public static PortletContext portletContext;

    /** Logger. */
    private static final Log logger = LogFactory.getLog(EtablissementService.class);
    public static long CACHE_TIMEOUT = 24 * 3600 * 1000; // One day


    public String pronoteEtablissementCheckUrl;
    public String pronoteEtablissementUrl;


    private static Map<String, Long> lastWSCheck = new ConcurrentHashMap<>();

    @PostConstruct
    private void postConstruct() {
        pronoteEtablissementUrl = System.getProperty("pronote.etablissement.url");
        if (pronoteEtablissementUrl == null)
            logger.warn("pronote.etablissement.url is not specified in properties");
        pronoteEtablissementCheckUrl = System.getProperty("pronote.etablissement.checkUrl");
        if (pronoteEtablissementCheckUrl == null)
            logger.warn("pronote.etablissement.checkUrl is not specified in properties");
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.index.cloud.ens.ext.etb.IEtablissementService#getEtablissement(java.lang.String)
     */


    public Application getApplication(String code) {

        if (code.startsWith(APPLICATION_ID_PREFIX)) {
            code = code.substring(APPLICATION_ID_PREFIX.length());
        }

        return getInternal(code, false);
    }

    /**
     * Do the main job
     * 
     * if needed, synchronize this the Etablissement WS
     *
     * @param clientID the client ID
     * @param refresh the refresh
     * @return the internal
     */
    private Application getInternal(String clientID, boolean create) {

        if (clientID.startsWith(PRONOTE_CLIENT_PREFIX)) {
            Long lastCheck = lastWSCheck.get(clientID);

            if (lastCheck == null || lastCheck + CACHE_TIMEOUT < System.currentTimeMillis() || create) {
                if (statusService.isReady(pronoteEtablissementCheckUrl)) {
                    boolean error = false;
                    long begin = System.currentTimeMillis();                    
                    //String codeEtablissement = clientID.substring(PRONOTE_CLIENT_PREFIX.length());
                    
                    String codeEtablissement = clientID;
                    try {
                        // Check etablissement

                        RestTemplate restTemplate = new RestTemplate();
                        
                        String url = pronoteEtablissementUrl.replaceAll("\\{idEtb\\}", codeEtablissement);
                        EtablissementResponse etablissement = restTemplate.getForObject(url, EtablissementResponse.class);

                        Application application = new Application(EtablissementService.APPLICATION_ID_PREFIX + clientID, etablissement.getNom());
                        repository.update(application);

                        lastWSCheck.put(clientID, System.currentTimeMillis());

                    } catch (HttpClientErrorException e) {
                        error = true;
                        
                        statusService.notifyError(pronoteEtablissementCheckUrl, new UnavailableServer(e.getStatusText()));
                        logger.error("can't retrieve etablissement #" + clientID, e);
                    } catch (Exception e) {
                        
                        error = true;
                        
                        logger.error("can't retrieve etablissement #" + clientID, e);
                    } finally   {
                        
                        long end = System.currentTimeMillis();
                        long elapsedTime = end - begin;

                        String name = "code='"+codeEtablissement+"'";
                        String src = "ETB" ;
                        profilerService.logEvent(src, name, elapsedTime, error);
     
                    }
                }
            }
        }   else    {
            if( create) {
                // Not a PRONOTE Application
                Application application = repository.getApplication(EtablissementService.APPLICATION_ID_PREFIX + clientID);
                if( application == null)    {
                    // Create with default name
                    application = new Application(EtablissementService.APPLICATION_ID_PREFIX + clientID, "Application "+ clientID);
                    repository.update(application);
                }
            }
         }

        // Get from repository
        Application application = repository.getApplication(EtablissementService.APPLICATION_ID_PREFIX + clientID);
        return application;
    }

    @Override
    public Application createByClientID(String code) {
        return getInternal(code, true);
    }

    @Override
    public void update(Application etablissement) {
        repository.update(etablissement);
    }

    @Override
    public Application getApplicationByClientID(String clientID) {
        return getInternal(clientID, false);

    }

    @Override
    public void deleteApplication(String code) {
        repository.delete(code);
     }


}
