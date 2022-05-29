package com.github.vkremianskii.pits.communicator.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.infra.RegistryCodecConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryConfig {

    @Bean
    RegistryClient registryClient(RegistryProperties properties,
                                  RegistryCodecConfigurer codecConfigurer) {
        return new RegistryClient(
            properties.getBaseUrl(),
            codecConfigurer);
    }

    @Bean
    RegistryCodecConfigurer registryCodecConfigurer(ObjectMapper objectMapper) {
        return new RegistryCodecConfigurer(objectMapper);
    }
}
