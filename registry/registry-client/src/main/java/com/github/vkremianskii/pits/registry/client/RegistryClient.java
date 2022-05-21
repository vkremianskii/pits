package com.github.vkremianskii.pits.registry.client;

import com.github.vkremianskii.pits.registry.types.UpdateEquipmentPositionRequest;
import com.github.vkremianskii.pits.registry.types.UpdateTruckPayloadWeightRequest;
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
                .bodyValue(new UpdateEquipmentPositionRequest(equipmentId, latitude, longitude, elevation))
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    public Mono<Void> updateTruckPayloadWeight(int equipmentId, int weight) {
        return webClient.post()
                .uri("/equipment/{id}/payload/weight", equipmentId)
                .contentType(APPLICATION_JSON)
                .bodyValue(new UpdateTruckPayloadWeightRequest(equipmentId, weight))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
