package org.imd.oauth2.resourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@Configuration
@EnableMethodSecurity()
public class AclConfig {

    // We expose MethodSecurityExpressionHandler using a static method to ensure that Spring publishes it
    // before it initializes Spring Security’s method security @Configuration classes.
    //
    // Injects ACL into sprint method security.

    @Bean
    static public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        final DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        final AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

    // We expose GrantedAuthorityDefaults using a static method to ensure that Spring publishes it
    // before it initializes Spring Security’s method security @Configuration classes.
    //
    // Prefix authorities with empty space, so they can be used without ROLE_ prefix or SCOPE_ prefix.
    // They look exactly as they are define in the token.

    @Bean
    static public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }



}
