package com.github.vkremianskii.pits.communicator.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.requireNonNull;

@Configuration
public class RegistryConfig {
    private final RegistryProperties properties;

    public RegistryConfig(RegistryProperties properties) {
        this.properties = requireNonNull(properties);
    }

    @Bean(name = "registry")
    WebClient registryClient() {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
