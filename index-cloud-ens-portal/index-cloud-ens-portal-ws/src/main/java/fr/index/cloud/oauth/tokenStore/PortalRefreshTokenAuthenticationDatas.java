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

public class PortalRefreshTokenAuthenticationDatas  {
    
 
    private String userName;
    private Map<String, String> requestParameters;
    private String clientId;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean approved;
    private Set<String> scope;
    private Set<String> resourceIds;
    private String redirectUri;
    private Set<String> responseTypes;
    private Map<String, Serializable> extensionProperties;

    public PortalRefreshTokenAuthenticationDatas() {

    }

    /**
     * For application before saving
     * 
     * @param value
     * @param authentication
     */
    public PortalRefreshTokenAuthenticationDatas( OAuth2Authentication authentication) {
        super();

        userName = (String) authentication.getPrincipal();
        requestParameters = authentication.getOAuth2Request().getRequestParameters();
        clientId = authentication.getOAuth2Request().getClientId();
        authorities = authentication.getOAuth2Request().getAuthorities();
        approved = authentication.getOAuth2Request().isApproved();
        scope = authentication.getOAuth2Request().getScope();
        resourceIds = authentication.getOAuth2Request().getResourceIds();
        redirectUri = authentication.getOAuth2Request().getRedirectUri();
        responseTypes = authentication.getOAuth2Request().getResponseTypes();
        extensionProperties = authentication.getOAuth2Request().getExtensions();
    }


    OAuth2Authentication getAuthentication() {

        Authentication userAuthentication = new PortalAuthentication(userName, "", new ArrayList());

        OAuth2Request request = new OAuth2Request(requestParameters, clientId, authorities, approved, scope, resourceIds, redirectUri, responseTypes,
                extensionProperties);

        return new OAuth2Authentication(request, userAuthentication);
    }

    /**
     * Getter for userName.
     * 
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Setter for userName.
     * 
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }


    /**
     * Getter for requestParameters.
     * 
     * @return the requestParameters
     */
    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }


    /**
     * Setter for requestParameters.
     * 
     * @param requestParameters the requestParameters to set
     */
    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }


    /**
     * Getter for clientId.
     * 
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }


    /**
     * Setter for clientId.
     * 
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }


    /**
     * Getter for approved.
     * 
     * @return the approved
     */
    public boolean isApproved() {
        return approved;
    }


    /**
     * Setter for approved.
     * 
     * @param approved the approved to set
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }


    /**
     * Getter for scope.
     * 
     * @return the scope
     */
    public Set<String> getScope() {
        return scope;
    }


    /**
     * Setter for scope.
     * 
     * @param scope the scope to set
     */
    public void setScope(Set<String> scope) {
        this.scope = scope;
    }


    /**
     * Getter for resourceIds.
     * 
     * @return the resourceIds
     */
    public Set<String> getResourceIds() {
        return resourceIds;
    }


    /**
     * Setter for resourceIds.
     * 
     * @param resourceIds the resourceIds to set
     */
    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }


    /**
     * Getter for redirectUri.
     * 
     * @return the redirectUri
     */
    public String getRedirectUri() {
        return redirectUri;
    }


    /**
     * Setter for redirectUri.
     * 
     * @param redirectUri the redirectUri to set
     */
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }


    /**
     * Getter for responseTypes.
     * 
     * @return the responseTypes
     */
    public Set<String> getResponseTypes() {
        return responseTypes;
    }


    /**
     * Setter for responseTypes.
     * 
     * @param responseTypes the responseTypes to set
     */
    public void setResponseTypes(Set<String> responseTypes) {
        this.responseTypes = responseTypes;
    }


    /**
     * Getter for extensionProperties.
     * 
     * @return the extensionProperties
     */
    public Map<String, Serializable> getExtensionProperties() {
        return extensionProperties;
    }


    /**
     * Setter for extensionProperties.
     * 
     * @param extensionProperties the extensionProperties to set
     */
    public void setExtensionProperties(Map<String, Serializable> extensionProperties) {
        this.extensionProperties = extensionProperties;
    }



}
