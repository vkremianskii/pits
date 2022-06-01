package com.github.vkremianskii.pits.processes.infra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.vkremianskii.pits.core.json.CoreModule;
import com.github.vkremianskii.pits.registry.json.RegistryModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return objectMapperBuilder -> objectMapperBuilder
            .modules(new CoreModule(), new RegistryModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();
    }
}
