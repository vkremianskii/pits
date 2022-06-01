package com.github.vkremianskii.pits.auth.client.security;

import com.github.vkremianskii.pits.auth.client.AuthClient;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.auth.model.Username.username;
import static java.util.Objects.requireNonNull;

@Component
public class AuthAppAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthClient authClient;

    public AuthAppAuthenticationManager(AuthClient authClient) {
        this.authClient = requireNonNull(authClient);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
        if (!authentication.getClass().equals(UsernamePasswordAuthenticationToken.class)) {
            throw new BadCredentialsException("Not a basic authentication: " + authentication.getClass());
        }

        final var basicAuthentication = (UsernamePasswordAuthenticationToken) authentication;
        final var username = basicAuthentication.getName();
        final var password = basicAuthentication.getCredentials().toString();

        return authClient.authenticateUser(username(username), password.toCharArray())
            .map(response -> new AuthAppAuthentication(response.userId(), response.scopes().stream()
                .map(scope -> new SimpleGrantedAuthority(scope.value))
                .toList()))
            .doOnNext(auth -> auth.setAuthenticated(true))
            .cast(Authentication.class);
    }
}
