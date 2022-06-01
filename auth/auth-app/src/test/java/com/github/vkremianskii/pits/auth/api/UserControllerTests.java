package com.github.vkremianskii.pits.auth.api;

import com.github.vkremianskii.pits.auth.dto.AuthenticateRequest;
import com.github.vkremianskii.pits.auth.dto.CreateUserRequest;
import com.github.vkremianskii.pits.auth.infra.InternalAuthenticationManager;
import com.github.vkremianskii.pits.auth.infra.JsonConfig;
import com.github.vkremianskii.pits.auth.infra.SecurityConfig;
import com.github.vkremianskii.pits.auth.logic.UserService;
import com.github.vkremianskii.pits.auth.model.UserId;
import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.core.web.error.UnauthorizedError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.TestAuthentication.basicAuthAdmin;
import static com.github.vkremianskii.pits.auth.TestUser.randomUserId;
import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.auth.util.AuthenticationUtils.basicAuth;
import static com.github.vkremianskii.pits.core.Tuple2.tuple2;
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
            .thenReturn(Mono.just(tuple2(randomUserId(), Set.of(scope("admin")))));
    }

    @Test
    void should_create_user() {
        // given
        var userId = randomUserId();
        when(userService.createUser(
            username("user"),
            "user".toCharArray(),
            Set.of(scope("scope")))).thenReturn(Mono.just(userId));

        // expect
        webClient.post()
            .uri("/user")
            .header(AUTHORIZATION, basicAuthAdmin())
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateUserRequest(
                username("user"),
                "user",
                Set.of(scope("scope"))))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.userId").isEqualTo(userId.value.toString());
        verify(userService).createUser(
            username("user"),
            "user".toCharArray(),
            Set.of(scope("scope")));
    }

    @Test
    void should_authenticate_user() {
        // given
        when(userService.authenticateUser(username("user"), "user".toCharArray()))
            .thenReturn(Mono.just(tuple2(
                UserId.valueOf("d7a8ba56-c335-4035-b781-942b4052c37e"),
                Set.of(scope("scope")))));

        // expect
        webClient.post()
            .uri("/user/auth")
            .header(AUTHORIZATION, basicAuth(username("user"), "user".toCharArray()))
            .contentType(APPLICATION_JSON)
            .bodyValue(new AuthenticateRequest(username("user"), "user"))
            .exchange()
            .expectStatus().isOk()
            .expectBody().json("""
                {
                    "userId": "d7a8ba56-c335-4035-b781-942b4052c37e",
                    "scopes": ["scope"]
                }
                """);
    }

    @Test
    void should_authenticate_user__unauthorized() {
        // given
        when(userService.authenticateUser(username("user"), "user".toCharArray()))
            .thenReturn(Mono.error((new UnauthorizedError())));

        // expect
        webClient.post()
            .uri("/user/auth")
            .header(AUTHORIZATION, basicAuth(username("user"), "user".toCharArray()))
            .contentType(APPLICATION_JSON)
            .bodyValue(new AuthenticateRequest(username("user"), "user"))
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
