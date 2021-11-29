package fr.index.cloud.oauth.enhancer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import fr.index.cloud.oauth.config.ClusterInfo;
import fr.index.cloud.oauth.config.SecurityFilter;

/**
 * Adapt token to cluster (include routeId and sessionId)
 */
public class AccessTokenEnhancer implements TokenEnhancer {

    /** The logger. */
    protected static Log logger = LogFactory.getLog(AccessTokenEnhancer.class);

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        if (logger.isDebugEnabled()) {
            logger.debug("/oauth/token enhance");

        }
        
        DefaultOAuth2AccessToken adaptedToken = new DefaultOAuth2AccessToken(accessToken);
        
        String routeId = System.getProperty("jvmroute");
        if( routeId == null)
            routeId = "";
        
        String sessionId = "";
        
        ClusterInfo cluster = ClusterInfo.instance.get();
        if (cluster != null) {


            // Create a new session for the ACCESS_TOKEN
            // (PORTALSESSIONID which is part of ACCESS_TOKEN must be unique)
            
            HttpServletRequest request = cluster.getRequest();

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            session = request.getSession(true);
            
            sessionId = session.getId();

            if (logger.isDebugEnabled()) {
                logger.debug("/oauth/token new session " + session.getId());

            }

        }

        adaptedToken.setValue(routeId + "_" + sessionId);
        
        return adaptedToken;
    }

}
