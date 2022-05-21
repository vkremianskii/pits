package com.github.vkremianskii.pits.registry.client;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.Location;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class RegistryClient {
    private final WebClient webClient;

    public RegistryClient(WebClient webClient) {
        this.webClient = requireNonNull(webClient);
    }

    public Mono<List<Equipment>> getEquipment() {
        return webClient.get()
                .uri("/equipment")
                .retrieve()
                .bodyToFlux(Equipment.class)
                .collectList();
    }

    public Mono<List<Location>> getLocations() {
        return webClient.get()
                .uri("/locations")
                .retrieve()
                .bodyToFlux(Location.class)
                .collectList();
    }
}
