package com.github.vkremianskii.pits.registry.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vkremianskii.pits.core.model.EquipmentId;
import com.github.vkremianskii.pits.core.model.EquipmentState;
import com.github.vkremianskii.pits.core.model.EquipmentType;
import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.core.model.LocationType;
import com.github.vkremianskii.pits.registry.dto.CreateEquipmentRequest;
import com.github.vkremianskii.pits.registry.dto.CreateEquipmentResponse;
import com.github.vkremianskii.pits.registry.dto.CreateLocationRequest;
import com.github.vkremianskii.pits.registry.dto.CreateLocationResponse;
import com.github.vkremianskii.pits.registry.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.dto.LocationsResponse;
import com.github.vkremianskii.pits.registry.dto.UpdateEquipmentStateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.vkremianskii.pits.registry.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static com.github.vkremianskii.pits.registry.infra.RegistryCodecConfigurer.configureCodecs;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class RegistryClient {

    private final WebClient webClient;

    public RegistryClient(RegistryProperties properties, ObjectMapper objectMapper) {
        webClient = WebClient.builder()
            .baseUrl(properties.baseUrl())
            .codecs(c -> configureCodecs(c, objectMapper))
            .build();
    }

    public Mono<EquipmentResponse> getEquipment() {
        return webClient.get()
            .uri("/equipment")
            .header(API_VERSION, EQUIPMENT_RESPONSE_OBJECT.toString())
            .retrieve()
            .bodyToMono(EquipmentResponse.class);
    }

    public Mono<CreateEquipmentResponse> createEquipment(String name, EquipmentType type) {
        return webClient.post()
            .uri("/equipment")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateEquipmentRequest(name, type))
            .retrieve()
            .bodyToMono(CreateEquipmentResponse.class);
    }

    public Mono<Void> updateEquipmentState(EquipmentId equipmentId, EquipmentState state) {
        return webClient.post()
            .uri("/equipment/{id}/state", equipmentId)
            .contentType(APPLICATION_JSON)
            .bodyValue(new UpdateEquipmentStateRequest(state))
            .retrieve()
            .toBodilessEntity()
            .then();
    }

    public Mono<LocationsResponse> getLocations() {
        return webClient.get()
            .uri("/location")
            .retrieve()
            .bodyToMono(LocationsResponse.class);
    }

    public Mono<CreateLocationResponse> createLocation(String name,
                                                       LocationType type,
                                                       List<LatLngPoint> geometry) {
        return webClient.post()
            .uri("/location")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest(name, type, geometry))
            .retrieve()
            .bodyToMono(CreateLocationResponse.class);
    }
}
