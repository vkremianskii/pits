package com.github.vkremianskii.pits.processes.integration;

import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

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
    RegistryCodecConfigurer registryCodecConfigurer(Jackson2ObjectMapperBuilder objectMapperBuilder) {
        return new RegistryCodecConfigurer(objectMapperBuilder);
    }
}
