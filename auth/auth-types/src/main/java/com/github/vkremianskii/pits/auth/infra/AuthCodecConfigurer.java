package com.github.vkremianskii.pits.auth.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

public class AuthCodecConfigurer {

    private AuthCodecConfigurer() {
    }

    public static void configureCodecs(CodecConfigurer configurer, ObjectMapper objectMapper) {
        final var codecs = configurer.defaultCodecs();
        codecs.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
        codecs.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
    }
}
