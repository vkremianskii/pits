package com.github.vkremianskii.pits.auth.client.security;

import com.github.vkremianskii.pits.auth.client.AuthClient;
import com.github.vkremianskii.pits.auth.model.Scope;
import com.github.vkremianskii.pits.auth.model.UserId;
import com.github.vkremianskii.pits.auth.model.Username;
import com.github.vkremianskii.pits.core.Tuple2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.core.Tuple2.tuple2;
import static java.util.Objects.requireNonNull;

@Component
public class AuthAppAuthenticationManager implements ReactiveAuthenticationManager {

    private static final Duration CACHE_DURATION = Duration.ofSeconds(10);

    private final AuthClient authClient;
    private final ConcurrentHashMap<Tuple2<Username, String>, Mono<Tuple2<UserId, Set<Scope>>>> principalCache = new ConcurrentHashMap<>();

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

        return fetchPrincipal(username(username), password)
            .map(principal -> new AuthAppAuthentication(principal.first(), principal.second().stream()
                .map(scope -> new SimpleGrantedAuthority(scope.value))
                .toList()))
            .doOnNext(auth -> auth.setAuthenticated(true))
            .cast(Authentication.class);
    }

    private Mono<Tuple2<UserId, Set<Scope>>> fetchPrincipal(Username username, String password) {
        return principalCache.computeIfAbsent(
                tuple2(username, password),
                key -> authClient.authenticateUser(username, password.toCharArray())
                    .map(response -> tuple2(response.userId(), response.scopes())))
            .cache(CACHE_DURATION);
    }
}
