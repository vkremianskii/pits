package com.github.vkremianskii.pits.registry.client;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.EquipmentState;
import com.github.vkremianskii.pits.core.types.model.EquipmentType;
import com.github.vkremianskii.pits.core.types.model.LatLngPoint;
import com.github.vkremianskii.pits.core.types.model.LocationType;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentRequest;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentResponse;
import com.github.vkremianskii.pits.registry.types.dto.CreateLocationRequest;
import com.github.vkremianskii.pits.registry.types.dto.CreateLocationResponse;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.types.dto.LocationsResponse;
import com.github.vkremianskii.pits.registry.types.dto.UpdateEquipmentStateRequest;
import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.vkremianskii.pits.registry.types.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.types.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class RegistryClient {

    private final WebClient webClient;

    public RegistryClient(String baseUrl, RegistryCodecConfigurer codecConfigurer) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .codecs(codecConfigurer::configureCodecs)
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
