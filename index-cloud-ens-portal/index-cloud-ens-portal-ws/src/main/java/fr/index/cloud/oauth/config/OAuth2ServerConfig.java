
package fr.index.cloud.oauth.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.tokens.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import fr.index.cloud.ens.application.api.Application;
import fr.index.cloud.ens.application.api.IApplicationService;
import fr.index.cloud.oauth.authentication.PortalUserDetailService;
import fr.index.cloud.oauth.enhancer.AccessTokenEnhancer;
import fr.index.cloud.oauth.tokenStore.IPortalTokenStore;
import fr.index.cloud.oauth.tokenStore.PortalAuthorizationCodeStore;
import fr.index.cloud.oauth.tokenStore.PortalTokenStore;
import fr.index.security.oauth.approval.PronoteApprovalHandler;
import fr.index.security.oauth.approval.PronoteApprovalStore;
import fr.index.security.oauth.services.mvc.AccessConfirmationController;




/**
 * Configuration de l'oAuth2
 *   - stockage des clients
 *   - types de token 
 * @author Jean-Sébastien
 */


@Configuration
public class OAuth2ServerConfig {

    private static final String CLOUD_RESOURCE_ID = "cloud";


    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(CLOUD_RESOURCE_ID).stateless(false);
        }


        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    // Since we want the protected resources to be accessible in the UI as well we need
                    // session creation to be allowed (it's disabled by default in 2.0.6)
                    .sessionManagement().sessionFixation().none().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().requestMatchers()
                    .antMatchers("/rest/Drive.**", "/rest/Admin.**", "/rest/User.getProfile", "/oauth/users/**", "/oauth/clients/**", "/me").and().authorizeRequests()
                    .antMatchers("/rest/User.getProfile").access("#oauth2.hasScope('drive')")
                    .antMatchers("/rest/Drive.**").access("#oauth2.hasScope('drive')")
                    .antMatchers("/rest/Admin.**").access("hasRole('ROLE_ADMIN')")
                    .antMatchers("/rest/User.signUp").permitAll().antMatchers("/Viewer.fileInfos").permitAll();


            // @formatter:on
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private TokenStore tokenStore;

        @Autowired
        private UserApprovalHandler userApprovalHandler;

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;


        @Autowired
        private AccessConfirmationController accessConfirmationController;

        @Autowired
        private ClientDetailsService clientDetailsService;
        @Autowired
        public ApprovalStore approvalStore;


        @Autowired
        ConsumerTokenServices tokenServices;

        @Autowired
        PortalUserDetailService userDetailService;
        
        @Autowired
        IApplicationService applicationService;
        

        public ClientDetailsService clientDetailsService() {
            return new ClientDetailsService() {

                @Override
                public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

                    Application application = applicationService.getApplicationByClientID(clientId);
                    
                    if (application != null) {
                        BaseClientDetails details = new BaseClientDetails();
                        details.setClientId(clientId);
                        details.setClientSecret(System.getProperty("pronote.oauth2.client.secret"));
                        details.setAuthorizedGrantTypes(Arrays.asList("password", "authorization_code", "refresh_token", "implicit"));
                        details.setScope(Arrays.asList("drive"));
                        details.setResourceIds(Arrays.asList("cloud"));
                        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
                        authorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
                        authorities.add(new SimpleGrantedAuthority("ROLE_TRUSTED_CLIENT"));
                        details.setAuthorities(authorities);
/*                        
                        // 10 secunds
                        details.setAccessTokenValiditySeconds(30);
                        // 3 mn
                        details.setRefreshTokenValiditySeconds(180);                        
*/                        
                        // 30 minutes
                        details.setAccessTokenValiditySeconds(1800);
                        // default one year
                        details.setRefreshTokenValiditySeconds(60 * 60 * 24 * 365);
                        
                        return details;
                    } else
                        throw new NoSuchClientException("Client with ID '" + clientId + "' not found");
                }
            };
        }


        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            accessConfirmationController.setClientDetailsService(clientDetailsService);
            accessConfirmationController.setApprovalStore(approvalStore);
            clients.withClientDetails(clientDetailsService());
      }

        @Bean
        public TokenStore tokenStore() {
              return new PortalTokenStore();
        }
        


        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            ITokenService tokenService = Locator.findMBean(ITokenService.class, ITokenService.MBEAN_NAME);
            endpoints.tokenEnhancer(new AccessTokenEnhancer()).tokenStore(tokenStore).userApprovalHandler(userApprovalHandler).authenticationManager(authenticationManager).authorizationCodeServices( new PortalAuthorizationCodeStore( tokenService));
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.realm("cloud/client").allowFormAuthenticationForClients();
        }

    }

    protected static class Stuff {

        @Autowired
        private ClientDetailsService clientDetailsService;

        @Autowired
        private IPortalTokenStore tokenStore;
        


        @Bean
        public ApprovalStore approvalStore() throws Exception {
            PronoteApprovalStore store = new PronoteApprovalStore();
            store.setPortalTokenStore(tokenStore);
            return store;
        }

        @Bean
        @Lazy
        @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public PronoteApprovalHandler userApprovalHandler() throws Exception {
            PronoteApprovalHandler handler = new PronoteApprovalHandler();
            handler.setApprovalStore(approvalStore());
            handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
            handler.setClientDetailsService(clientDetailsService);
            handler.setUseApprovalStore(true);
            return handler;
        }


    }


}
