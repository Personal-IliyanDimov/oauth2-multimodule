package org.imd.oauth2.resourceserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity()
@EnableMethodSecurity()
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    // @formatter:off
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
             .oauth2ResourceServer()
                .jwt(Customizer.withDefaults())
             .and()
             .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                     .requestMatchers("/posts").fullyAuthenticated()
                     .requestMatchers("/posts/*").fullyAuthenticated()
                     .requestMatchers("/posts/*/comments").fullyAuthenticated()
                     .requestMatchers("/posts/*/comments/**").fullyAuthenticated()
                     .anyRequest().denyAll()
              );

        return http.build();
    }
    // @formatter:on

}