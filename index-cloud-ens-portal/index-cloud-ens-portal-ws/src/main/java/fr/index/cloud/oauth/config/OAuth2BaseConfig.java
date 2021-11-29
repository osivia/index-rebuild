package fr.index.cloud.oauth.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;

@Configuration
@Order(-1)

public class OAuth2BaseConfig extends AuthorizationServerSecurityConfiguration {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
       super.configure(http);
       // Session fixation not supported by JBoss AS
       http.sessionManagement().sessionFixation().none();
       
    }
}