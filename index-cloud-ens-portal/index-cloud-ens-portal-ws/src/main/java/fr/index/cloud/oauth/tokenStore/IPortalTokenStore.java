package fr.index.cloud.oauth.tokenStore;

import java.util.Collection;

import org.springframework.security.oauth2.provider.token.TokenStore;


public interface IPortalTokenStore extends TokenStore {
    

    /**
     * get user refresh tokens
     * 
     * @param userName
     * @return
     */
    Collection<AggregateRefreshTokenInfos> findTokensByUserName(String userName) ;

}
