package com.github.vkremianskii.pits.auth.client.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@Import(AuthAppAuthenticationManager.class)
public class AuthAppSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         AuthAppAuthenticationManager authenticationManager) {
        http.authorizeExchange(exchanges -> exchanges
                .anyExchange().authenticated()
            )
            .authenticationManager(authenticationManager)
            .httpBasic(withDefaults())
            .csrf().disable();

        return http.build();
    }
}
