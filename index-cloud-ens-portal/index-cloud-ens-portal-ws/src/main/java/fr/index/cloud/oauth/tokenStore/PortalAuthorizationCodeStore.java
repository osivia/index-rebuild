package fr.index.cloud.oauth.tokenStore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.osivia.portal.api.tokens.ITokenService;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;



/**
 * The Class PortalAuthorizationCodeStore.
 * 
 * Stores the authorisation code by using portal tokenService (to preserve cluster)
 */
public class PortalAuthorizationCodeStore implements AuthorizationCodeServices {

    protected final ConcurrentHashMap<String, String> authorizationCodeStore = new ConcurrentHashMap<String, String>();
    

    /** The logger. */
    protected static Log logger = LogFactory.getLog(PortalAuthorizationCodeStore.class);

    ITokenService tokenService;


    public PortalAuthorizationCodeStore(ITokenService tokenService) {
        super();
        this.tokenService = tokenService;
    }


    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = null;

        try {
            PortalRefreshTokenAuthenticationDatas datas = new PortalRefreshTokenAuthenticationDatas(authentication);
            String jsonData = new ObjectMapper().writeValueAsString(datas);
            Map<String, String> map = new HashMap<String, String>();
            map.put("value", jsonData);
            

            
            code = this.tokenService.generateToken(map);
            if( logger.isDebugEnabled())    {
                logger.debug("createAuthorizationCode " + code + "-> " +jsonData);
            }            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {

        if( logger.isDebugEnabled())    {
            logger.debug("consumeAuthorizationCode " + code);
        }
        
        OAuth2Authentication auth = null;

        try {
            int nbTries = 0;
            Map<String, String> map = null;
            
            do {
                map = this.tokenService.validateToken(code);
                nbTries++;
                
                if (map == null) {
                    // Update is asynchronous on cluster
                    Thread.sleep(500L);
                }

            } while ( (map == null) && (nbTries < 5));

            
            if (map != null) {
                
                if( logger.isDebugEnabled())    {
                    logger.debug("consumeAuthorizationCode " + code + "-> " +map.get("value"));
                }   
                
                String jsonData = map.get("value");
                if (jsonData != null) {
                    PortalRefreshTokenAuthenticationDatas datas = new ObjectMapper().readValue(jsonData, PortalRefreshTokenAuthenticationDatas.class);
                    auth = datas.getAuthentication();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        }

        return auth;
    }


}
