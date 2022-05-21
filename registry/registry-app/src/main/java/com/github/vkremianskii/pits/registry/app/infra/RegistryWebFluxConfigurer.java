package com.github.vkremianskii.pits.registry.app.infra;

import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class RegistryWebFluxConfigurer implements WebFluxConfigurer {
    @Autowired
    private Jackson2ObjectMapperBuilder objectMapperBuilder;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        new RegistryCodecConfigurer(objectMapperBuilder).configureCodecs(configurer);
    }
}
