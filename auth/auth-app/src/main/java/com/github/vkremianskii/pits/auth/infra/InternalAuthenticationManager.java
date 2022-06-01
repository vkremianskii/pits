package com.github.vkremianskii.pits.auth.infra;

import com.github.vkremianskii.pits.auth.logic.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.auth.model.Username.username;
import static java.util.Objects.requireNonNull;

@Component
public class InternalAuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    public InternalAuthenticationManager(UserService userService) {
        this.userService = requireNonNull(userService);
    }


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!authentication.getClass().equals(UsernamePasswordAuthenticationToken.class)) {
            throw new BadCredentialsException("Not a basic authentication: " + authentication.getClass());
        }

        final var basicAuthentication = (UsernamePasswordAuthenticationToken) authentication;
        final var username = basicAuthentication.getName();
        final var password = basicAuthentication.getCredentials().toString();

        return userService.authenticateUser(username(username), password.toCharArray())
            .map(scopes -> new InternalAuthentication(scopes.stream()
                .map(scope -> new SimpleGrantedAuthority(scope.value))
                .toList()))
            .doOnNext(auth -> auth.setAuthenticated(true))
            .cast(Authentication.class);
    }
}
