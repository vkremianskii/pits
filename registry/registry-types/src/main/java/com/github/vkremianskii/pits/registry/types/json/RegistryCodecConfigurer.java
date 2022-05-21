package com.github.vkremianskii.pits.registry.types.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.vkremianskii.pits.registry.types.json.deserializer.EquipmentDeserializer;
import com.github.vkremianskii.pits.registry.types.json.deserializer.LocationDeserializer;
import com.github.vkremianskii.pits.registry.types.json.serializer.EquipmentStateSerializer;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.Location;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static java.util.Objects.requireNonNull;

public class RegistryCodecConfigurer {
    private final Jackson2ObjectMapperBuilder objectMapperBuilder;

    public RegistryCodecConfigurer(Jackson2ObjectMapperBuilder objectMapperBuilder) {
        this.objectMapperBuilder = requireNonNull(objectMapperBuilder);
    }

    public void configureCodecs(CodecConfigurer configurer) {
        final var mapper = objectMapperBuilder
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .serializerByType(TruckState.class, new EquipmentStateSerializer())
                .deserializerByType(Equipment.class, new EquipmentDeserializer())
                .deserializerByType(Location.class, new LocationDeserializer())
                .build();

        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
    }
}
