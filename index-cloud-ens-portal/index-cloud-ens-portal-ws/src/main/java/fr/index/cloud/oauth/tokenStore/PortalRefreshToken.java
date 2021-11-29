package fr.index.cloud.oauth.tokenStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import fr.index.cloud.oauth.authentication.PortalAuthentication;

public class PortalRefreshToken implements OAuth2RefreshToken {

    private String value;
    

    public PortalRefreshToken() {

    }

    /**
     * For application before saving
     * 
     * @param value
     * @param authentication
     */
    public PortalRefreshToken(String value) {
        super();
        this.value = value;
    }








    /**
     * Getter for value.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }


    /**
     * Setter for value.
     * 
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
