package com.github.vkremianskii.pits.auth.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vkremianskii.pits.auth.dto.AuthenticateRequest;
import com.github.vkremianskii.pits.auth.dto.AuthenticateResponse;
import com.github.vkremianskii.pits.auth.dto.CreateUserRequest;
import com.github.vkremianskii.pits.auth.dto.CreateUserResponse;
import com.github.vkremianskii.pits.auth.model.Scope;
import com.github.vkremianskii.pits.auth.model.Username;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.infra.AuthCodecConfigurer.configureCodecs;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(AuthProperties properties, ObjectMapper objectMapper) {
        webClient = WebClient.builder()
            .baseUrl(properties.baseUrl())
            .codecs(c -> configureCodecs(c, objectMapper))
            .build();
    }

    public Mono<CreateUserResponse> createUser(Username username, char[] password, Set<Scope> scopes) {
        return webClient.post()
            .uri("/user")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateUserRequest(username, String.valueOf(password), scopes))
            .retrieve()
            .bodyToMono(CreateUserResponse.class);
    }

    public Mono<AuthenticateResponse> authenticateUser(Username username, char[] password) {
        return webClient.post()
            .uri("/user/auth")
            .contentType(APPLICATION_JSON)
            .bodyValue(new AuthenticateRequest(username, String.valueOf(password)))
            .retrieve()
            .bodyToMono(AuthenticateResponse.class);
    }
}
