package com.github.vkremianskii.pits.communicator.app.integration;

import com.github.vkremianskii.pits.registry.client.RegistryClient;
import org.springframework.beans.factory.annotation.Qualifier;
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
    WebClient registryWebClient() {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Bean
    RegistryClient registryClient(@Qualifier("registry") WebClient webClient) {
        return new RegistryClient(webClient);
    }
}
