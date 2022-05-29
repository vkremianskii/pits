package com.github.vkremianskii.pits.registry.app.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vkremianskii.pits.registry.types.infra.RegistryCodecConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import static java.util.Objects.requireNonNull;

@Configuration
public class RegistryWebFluxConfigurer implements WebFluxConfigurer {

    private final ObjectMapper objectMapper;

    public RegistryWebFluxConfigurer(ObjectMapper objectMapper) {
        this.objectMapper = requireNonNull(objectMapper);
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        new RegistryCodecConfigurer(objectMapper).configureCodecs(configurer);
    }
}
