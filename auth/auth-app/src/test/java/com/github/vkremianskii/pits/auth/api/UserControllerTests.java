package com.github.vkremianskii.pits.auth.api;

import com.github.vkremianskii.pits.auth.dto.AuthenticateRequest;
import com.github.vkremianskii.pits.auth.dto.CreateUserRequest;
import com.github.vkremianskii.pits.auth.infra.InternalAuthenticationManager;
import com.github.vkremianskii.pits.auth.infra.JsonConfig;
import com.github.vkremianskii.pits.auth.infra.SecurityConfig;
import com.github.vkremianskii.pits.auth.logic.UserService;
import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.TestAuthentication.basicAuth;
import static com.github.vkremianskii.pits.auth.TestUser.randomUserId;
import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = UserController.class)
@Import({
    CoreWebAutoConfiguration.class,
    JsonConfig.class,
    SecurityConfig.class,
    InternalAuthenticationManager.class})
class UserControllerTests {

    @MockBean
    UserService userService;
    @Autowired
    WebTestClient webClient;

    @BeforeEach
    void setup() {
        when(userService.authenticateUser(username("admin"), "admin".toCharArray()))
            .thenReturn(Mono.just(Set.of(scope("scope"))));
    }

    @Test
    void should_create_user() {
        // given
        var userId = randomUserId();
        when(userService.createUser(
            username("username"),
            "password".toCharArray(),
            Set.of(scope("scope")))).thenReturn(Mono.just(userId));

        // expect
        webClient.post()
            .uri("/user")
            .header(AUTHORIZATION, basicAuth(username("admin"), "admin".toCharArray()))
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateUserRequest(
                username("username"),
                "password",
                Set.of(scope("scope"))))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.userId").isEqualTo(userId.value.toString());
        verify(userService).createUser(
            username("username"),
            "password".toCharArray(),
            Set.of(scope("scope")));
    }

    @Test
    void should_authenticate_user() {
        // given
        when(userService.authenticateUser(username("username"), "password".toCharArray()))
            .thenReturn(Mono.just(Set.of(scope("scope"))));

        // expect
        webClient.post()
            .uri("/user/auth")
            .header(AUTHORIZATION, basicAuth(username("username"), "password".toCharArray()))
            .contentType(APPLICATION_JSON)
            .bodyValue(new AuthenticateRequest(username("username"), "password"))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.scopes[0]").isEqualTo("scope");
    }
}
