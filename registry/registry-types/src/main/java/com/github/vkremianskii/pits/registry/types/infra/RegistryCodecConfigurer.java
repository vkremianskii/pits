package com.github.vkremianskii.pits.registry.types.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

import static java.util.Objects.requireNonNull;

public class RegistryCodecConfigurer {

    private final ObjectMapper objectMapper;

    public RegistryCodecConfigurer(ObjectMapper objectMapper) {
        this.objectMapper = requireNonNull(objectMapper);
    }

    public void configureCodecs(CodecConfigurer configurer) {
        final var codecs = configurer.defaultCodecs();
        codecs.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
        codecs.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
    }
}
