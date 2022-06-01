package com.github.vkremianskii.pits.auth.client.security;

import com.github.vkremianskii.pits.auth.client.AuthClient;
import com.github.vkremianskii.pits.auth.dto.AuthenticateResponse;
import com.github.vkremianskii.pits.auth.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthAppAuthenticationManagerTests {

    AuthClient authClient = mock(AuthClient.class);
    AuthAppAuthenticationManager sut = new AuthAppAuthenticationManager(authClient);

    @Test
    void should_fetch_and_cache_principal_from_auth_app() {
        // given
        var userId = UserId.valueOf("a51b4cc4-ccc2-451e-9bed-c552e9ac6320");
        when(authClient.authenticateUser(username("user"), "user".toCharArray()))
            .thenReturn(Mono.just(new AuthenticateResponse(userId, Set.of(scope("scope")))));

        // when
        var auth1 = sut.authenticate(new UsernamePasswordAuthenticationToken("user", "user")).block();
        var auth2 = sut.authenticate(new UsernamePasswordAuthenticationToken("user", "user")).block();

        // then
        assertThat(auth1).isInstanceOf(AuthAppAuthentication.class);
        assertThat(auth1.getPrincipal()).isEqualTo(userId);
        assertThat(auth1.getCredentials()).isEqualTo("ERASED");
        assertThat(auth1.getAuthorities()).hasSize(1);
        assertThat(auth1.getAuthorities().stream().toList().get(0)).isEqualTo(new SimpleGrantedAuthority("scope"));
        assertThat(auth2).isEqualTo(auth1);
        verify(authClient, times(1)).authenticateUser(username("user"), "user".toCharArray());
    }
}
