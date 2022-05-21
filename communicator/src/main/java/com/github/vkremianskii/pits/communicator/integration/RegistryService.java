package com.github.vkremianskii.pits.communicator.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@Component
public class RegistryService {
    private final RegistryProperties properties;
    private final WebClient webClient;

    public RegistryService(RegistryProperties properties,
                           @Qualifier("registry") WebClient webClient) {
        this.properties = requireNonNull(properties);
        this.webClient = requireNonNull(webClient);
    }

    public Mono<Void> updateEquipmentPosition(int equipmentId,
                                              double latitude,
                                              double longitude,
                                              int elevation) {
        return webClient.post()
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
