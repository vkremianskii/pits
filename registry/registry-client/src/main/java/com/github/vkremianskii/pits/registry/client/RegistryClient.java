package com.github.vkremianskii.pits.registry.client;

import com.github.vkremianskii.pits.registry.types.UpdateEquipmentPositionRequest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class RegistryClient {
    private final WebClient webClient;

    public RegistryClient(WebClient webClient) {
        this.webClient = requireNonNull(webClient);
    }

    public Mono<Void> updateEquipmentPosition(int equipmentId,
                                              double latitude,
                                              double longitude,
                                              int elevation) {
        return webClient.post()
                .uri("/equipment/{id}/position", equipmentId)
                .contentType(APPLICATION_JSON)
                .bodyValue(new UpdateEquipmentPositionRequest(latitude, longitude, elevation))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
