package com.github.vkremianskii.pits.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         InternalAuthenticationManager authenticationManager) {
        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/user/auth").permitAll()
                .anyExchange().authenticated()
            )
            .authenticationManager(authenticationManager)
            .httpBasic(withDefaults())
            .csrf().disable();

        return http.build();
    }
}
