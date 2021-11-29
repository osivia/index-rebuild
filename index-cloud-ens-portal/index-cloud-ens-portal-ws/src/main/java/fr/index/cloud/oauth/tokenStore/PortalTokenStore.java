package fr.index.cloud.oauth.tokenStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.PropertyList;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import fr.index.cloud.ens.ws.DriveRestController;
import fr.index.cloud.ens.ws.commands.GetUserProfileCommand;
import fr.index.cloud.oauth.commands.GetRefreshTokenCommand;
import fr.index.cloud.oauth.commands.RemoveRefreshTokenCommand;
import fr.index.cloud.oauth.commands.StoreRefreshTokenCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;


/**
 * Portal store for tokens
 * 
 * access tokens are stored in memory
 * refresh tokens are stored in user profile
 * 
 * @author Jean-Sébastien Steux
 */

public class PortalTokenStore extends InMemoryTokenStore implements IPortalTokenStore {


    /**
     * getNuxeoController
     * 
     * @return
     */
    private NuxeoController getNuxeoController() {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }
    
    /** Logger. */
    private static final Log logger = LogFactory.getLog(PortalTokenStore.class);


    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        try {
            NuxeoController nuxeoController = getNuxeoController();

            PortalRefreshTokenAuthenticationDatas datas = new PortalRefreshTokenAuthenticationDatas(authentication);
            String jsonData = new ObjectMapper().writeValueAsString(datas);

            String userName = (String) authentication.getPrincipal();
            Document userWorkspace = (Document) nuxeoController.executeNuxeoCommand(new GetUserProfileCommand(userName));
            
            Date date = null;
            if( refreshToken instanceof ExpiringOAuth2RefreshToken)
                date = ((ExpiringOAuth2RefreshToken)refreshToken).getExpiration();
            
            

            nuxeoController.executeNuxeoCommand(new StoreRefreshTokenCommand(userWorkspace.getPath(), refreshToken.getValue(), date, jsonData));
            
          
            // Clean old refresh tokens
            // Due to DefaultTokenServices.createAccessToken            
            //   if refreshed token is not stored on client side and the server is rebooted
            //   this methode creates new refresh tokens
            //   -> It may lead to new tokens
            removeOlderRefreshTokens(nuxeoController, datas, userName);
            

        } catch (Exception e) {
            throw new RuntimeException("can't serialize refresh token", e);
        }

    }


    /**
     * Removes the older refresh tokens.
     *
     * @param nuxeoController the nuxeo controller
     * @param datas the datas
     * @param userName the user name
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     */
    private void removeOlderRefreshTokens(NuxeoController nuxeoController, PortalRefreshTokenAuthenticationDatas datas, String userName)
            throws IOException, JsonParseException, JsonMappingException {
        
       
        Document userWorkspace = (Document) nuxeoController.executeNuxeoCommand(new GetUserProfileCommand(userName));
        PropertyList tokens = userWorkspace.getProperties().getList("oatk:tokens");      
        
        List<String> refreshTokens = new ArrayList<>();
        
        /* Search for tokens with same scope/client */
        
        for (int i = 0; i < tokens.size(); i++) {
            PropertyMap tokenMap = tokens.getMap(i);
            String value = tokenMap.getString("value");
            String tokenJsonData = tokenMap.getString("authentication");
            
            if (tokenJsonData != null) {
                PortalRefreshTokenAuthenticationDatas tokenDatas = new ObjectMapper().readValue(tokenJsonData, PortalRefreshTokenAuthenticationDatas.class);
                if( ObjectUtils.equals(tokenDatas.getScope(),datas.getScope()))  {
                    if( ObjectUtils.equals(tokenDatas.getClientId(),datas.getClientId()))   {
                        refreshTokens.add(value);
                    }
                }
            }
        }
        
        /* If numbers of similar tokens > 10, Keep only 3 last refresh tokens  */
            
        
        if( refreshTokens.size() > 3)  {
            logger.info("purging refreshed tokens for user "+ userName);
            for (int i=0 ; i< refreshTokens.size() - 3; i++) {
                removeRefreshToken(refreshTokens.get(i));
            }
            
        }
    }


    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        try {

            NuxeoController nuxeoController = getNuxeoController();
            
            Documents docs = (Documents) nuxeoController.executeNuxeoCommand(new GetRefreshTokenCommand(token));
            
            if (docs.size() > 1)
                throw new RuntimeException("more than one token detected");
            
            if (docs.size() == 1) {
                PropertyList tokens = docs.get(0).getProperties().getList("oatk:tokens");
                for (int i = 0; i < tokens.size(); i++) {
                    PropertyMap tokenMap = tokens.getMap(i);
                    String value = tokenMap.getString("value");
                    if (token.equals(value)) {
                        String jsonData = tokenMap.getString("authentication");
                        if (jsonData != null) {
                            PortalRefreshTokenAuthenticationDatas datas = new ObjectMapper().readValue(jsonData, PortalRefreshTokenAuthenticationDatas.class);

                            OAuth2Authentication authAuthentication = datas.getAuthentication();

                            OAuth2Request storedRequest = authAuthentication.getOAuth2Request();
                            Authentication userAuthentication = authAuthentication.getUserAuthentication();

                            OAuth2Authentication authentication = new OAuth2Authentication(storedRequest, userAuthentication);
       
                            return authentication;
                        }
                    }
                }
              }

        } catch (Exception e) {
            throw new RuntimeException("can't serialize refresh token", e);
        }
        return null;
    }


    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        try {

            NuxeoController nuxeoController = getNuxeoController();
            Documents docs = (Documents) nuxeoController.executeNuxeoCommand(new GetRefreshTokenCommand(tokenValue));

            if (docs.size() > 1)
                throw new RuntimeException("more then one token detected");

            if (docs.size() == 1) {
                PropertyList tokens = docs.get(0).getProperties().getList("oatk:tokens");
                for (int i = 0; i < tokens.size(); i++) {
                    PropertyMap tokenMap = tokens.getMap(i);
                    String value = tokenMap.getString("value");
                    if (tokenValue.equals(value)) {
                        PortalRefreshToken refreshToken = null;
                        Date expirationDate = tokenMap.getDate("expiration");
                        if (expirationDate == null)
                            refreshToken = new PortalRefreshToken(tokenValue);
                        else
                            refreshToken = new PortalExpiringRefreshToken(tokenValue, expirationDate);
                        return refreshToken;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("can't serialize refresh token", e);
        }
        return null;
    }


    @Override
    public void removeRefreshToken(String tokenValue) {
        NuxeoController nuxeoController = getNuxeoController();

        Documents docs = (Documents) nuxeoController.executeNuxeoCommand(new GetRefreshTokenCommand(tokenValue));
        
        if (docs.size() > 1)
            throw new RuntimeException("more than one token detected");
        
        if (docs.size() == 1) {
            Document doc = docs.get(0);
            PropertyList tokens = doc.getProperties().getList("oatk:tokens");
            for (int i = 0; i < tokens.size(); i++) {
                PropertyMap tokenMap = tokens.getMap(i);
                String value = tokenMap.getString("value");
                if (tokenValue.equals(value)) {
                    nuxeoController.executeNuxeoCommand(new RemoveRefreshTokenCommand(doc.getPath(), tokenValue, i));
                }
            }
        }
    }


    @Override
    public Collection<AggregateRefreshTokenInfos> findTokensByUserName(String userName)  {
        List<AggregateRefreshTokenInfos> refreshTokens = new ArrayList<AggregateRefreshTokenInfos>();
        try {

            NuxeoController nuxeoController = getNuxeoController();

            Document userWorkspace = (Document) nuxeoController.executeNuxeoCommand(new GetUserProfileCommand(userName));
            if (userWorkspace != null) {

                PropertyList tokens = userWorkspace.getProperties().getList("oatk:tokens");
                for (int i = 0; i < tokens.size(); i++) {
                    PropertyMap tokenMap = tokens.getMap(i);
                    String value = tokenMap.getString("value");
                    String jsonData = tokenMap.getString("authentication");
                    Date expirationDate = tokenMap.getDate("expiration");                    
                    if (jsonData != null) {
                        PortalRefreshTokenAuthenticationDatas datas = new ObjectMapper().readValue(jsonData, PortalRefreshTokenAuthenticationDatas.class);

                        refreshTokens.add(new AggregateRefreshTokenInfos(datas, value, expirationDate));
                    }
                }
            }


        } catch (Exception e) {
            throw new RuntimeException("can't serialize refresh token", e);
        }
        return refreshTokens;
    }

    
    @Override  
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        
        // In case of re-authentification, the refresh token is in the inMemoryCache
        // but may have been revoked from the dashpboard UI
        
        OAuth2AccessToken accessToken = super.getAccessToken(authentication);
        if( accessToken != null) {
            OAuth2RefreshToken refreshToken = accessToken.getRefreshToken() ;
            if( refreshToken == null)
                accessToken = null;
            else {
                OAuth2RefreshToken actualRefreshToken = readRefreshToken(refreshToken.getValue());
                if( actualRefreshToken == null)
                    accessToken = null;
                
            }
        }  
        
        return accessToken;
    }

}
