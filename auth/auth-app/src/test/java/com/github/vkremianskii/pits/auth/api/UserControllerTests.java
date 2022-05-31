package com.github.vkremianskii.pits.auth.api;

import com.github.vkremianskii.pits.auth.dto.AuthenticateRequest;
import com.github.vkremianskii.pits.auth.dto.CreateUserRequest;
import com.github.vkremianskii.pits.auth.logic.UserService;
import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.UserId.userId;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = UserController.class)
@Import(CoreWebAutoConfiguration.class)
class UserControllerTests {

    @MockBean
    UserService userService;
    @Autowired
    WebTestClient webClient;

    @Test
    void should_create_user() {
        // given
        var userId = userId(randomUUID());
        when(userService.createUser(
            username("username"),
            "password".toCharArray(),
            Set.of(scope("scope")))).thenReturn(Mono.just(userId));

        // expect
        webClient.post()
            .uri("/user")
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
        when(userService.authenticate(username("username"), "password".toCharArray()))
            .thenReturn(Mono.just(Set.of(scope("scope"))));

        // expect
        webClient.post()
            .uri("/user/auth")
            .contentType(APPLICATION_JSON)
            .bodyValue(new AuthenticateRequest(username("username"), "password"))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.scopes[0]").isEqualTo("scope");
        verify(userService).authenticate(username("username"), "password".toCharArray());
    }
}
