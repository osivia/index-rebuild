package fr.index.cloud.oauth.tokenStore;

import java.util.Date;

/**
 * Aggregate  token (value + authentication + expiration)
 * 
 * @author Jean-Sébastien
 */
public class AggregateRefreshTokenInfos {

    
    public AggregateRefreshTokenInfos(PortalRefreshTokenAuthenticationDatas authentication, String value, Date expirationDate) {
        super();
        this.authentication = authentication;
        this.value = value;
        this.expirationDate = expirationDate;
    }

    PortalRefreshTokenAuthenticationDatas authentication;
    String value;
    final Date expirationDate;
    
    
    /**
     * Getter for expirationDate.
     * @return the expirationDate
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Getter for value.
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Setter for tokenDatas.
     * @param tokenDatas the tokenDatas to set
     */
    public PortalRefreshTokenAuthenticationDatas getAuthentication() {
        return authentication;
    }

}
