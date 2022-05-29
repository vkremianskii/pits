package com.github.vkremianskii.pits.core.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.vkremianskii.pits.core.types.json.CoreTypesModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    CoreExceptionHandler.class,
    RequestLoggingFilter.class})
public class CoreWebAutoConfiguration {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return objectMapperBuilder -> objectMapperBuilder
            .modules(new CoreTypesModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();
    }
}
