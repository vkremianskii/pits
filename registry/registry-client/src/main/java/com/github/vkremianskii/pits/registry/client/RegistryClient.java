package com.github.vkremianskii.pits.registry.client;

import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentRequest;
import com.github.vkremianskii.pits.registry.types.dto.UpdateEquipmentStateRequest;
import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Location;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.scheduler.Schedulers.boundedElastic;

public class RegistryClient {
    private final WebClient webClient;

    public RegistryClient(String baseUrl, RegistryCodecConfigurer codecConfigurer) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(codecConfigurer::configureCodecs)
                .build();
    }

    public Mono<List<Equipment>> getEquipment() {
        return webClient.get()
                .uri("/equipment")
                .retrieve()
                .bodyToFlux(Equipment.class)
                .collectList()
                .subscribeOn(boundedElastic());
    }

    public Mono<Void> createEquipment(String name, EquipmentType type) {
        return webClient.post()
                .uri("/equipment")
                .contentType(APPLICATION_JSON)
                .bodyValue(new CreateEquipmentRequest(name, type))
                .retrieve()
                .toBodilessEntity()
                .then()
                .subscribeOn(boundedElastic());
    }

    public Mono<Void> updateEquipmentState(int equipmentId, EquipmentState state) {
        return webClient.post()
                .uri("/equipment/{id}/state", equipmentId)
                .contentType(APPLICATION_JSON)
                .bodyValue(new UpdateEquipmentStateRequest(state))
                .retrieve()
                .toBodilessEntity()
                .then()
                .subscribeOn(boundedElastic());
    }

    public Mono<List<Location>> getLocations() {
        return webClient.get()
                .uri("/locations")
                .retrieve()
                .bodyToFlux(Location.class)
                .collectList()
                .subscribeOn(boundedElastic());
    }
}
