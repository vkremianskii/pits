import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vkremianskii.pits.auth.dto.CreateUserRequest;
import com.github.vkremianskii.pits.auth.dto.CreateUserResponse;
import com.github.vkremianskii.pits.auth.dto.GetUsersResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.auth.infra.AuthCodecConfigurer.configureCodecs;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class AuthClient {

    private final WebClient webClient;

    public AuthClient(AuthProperties properties, ObjectMapper objectMapper) {
        webClient = WebClient.builder()
            .baseUrl(properties.baseUrl())
            .codecs(c -> configureCodecs(c, objectMapper))
            .build();
    }

    public Mono<GetUsersResponse> getUsers() {
        return webClient.get()
            .uri("/user")
            .retrieve()
            .bodyToMono(GetUsersResponse.class);
    }

    public Mono<CreateUserResponse> createUser() {
        return webClient.post()
            .uri("/user")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateUserRequest())
            .retrieve()
            .bodyToMono(CreateUserResponse.class);
    }
}
