package com.github.vkremianskii.pits.registry.app.infra;

import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import static java.util.Objects.requireNonNull;

@Configuration
public class RegistryWebFluxConfigurer implements WebFluxConfigurer {
    private final Jackson2ObjectMapperBuilder objectMapperBuilder;

    public RegistryWebFluxConfigurer(Jackson2ObjectMapperBuilder objectMapperBuilder) {
        this.objectMapperBuilder = requireNonNull(objectMapperBuilder);
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        new RegistryCodecConfigurer(objectMapperBuilder).configureCodecs(configurer);
    }
}
