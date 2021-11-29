package fr.index.cloud.oauth.tokenStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import fr.index.cloud.oauth.authentication.PortalAuthentication;

public class PortalExpiringRefreshToken extends PortalRefreshToken implements Serializable,  ExpiringOAuth2RefreshToken{

    private static final long serialVersionUID = 4999653766590648859L;

 
    private final Date expiration;

    /**
     * @param value
     */
    public PortalExpiringRefreshToken(String value, Date expiration) {
        super(value);
        this.expiration = expiration;
    }

    /**
     * The instant the token expires.
     * 
     * @return The instant the token expires.
     */
    public Date getExpiration() {
        return expiration;
    }

  

}
