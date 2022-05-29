package com.github.vkremianskii.pits.core.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.vkremianskii.pits.core.types.model.Equipment;
import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.Location;
import com.github.vkremianskii.pits.core.types.model.LocationId;
import com.github.vkremianskii.pits.core.types.model.equipment.DozerState;
import com.github.vkremianskii.pits.core.types.model.equipment.DrillState;
import com.github.vkremianskii.pits.core.types.model.equipment.ShovelState;
import com.github.vkremianskii.pits.core.types.model.equipment.TruckState;
import com.github.vkremianskii.pits.core.types.json.deserializer.EquipmentDeserializer;
import com.github.vkremianskii.pits.core.types.json.deserializer.EquipmentStateDeserializer;
import com.github.vkremianskii.pits.core.types.json.deserializer.LocationDeserializer;
import com.github.vkremianskii.pits.core.types.json.deserializer.MicrotypeDeserializer;
import com.github.vkremianskii.pits.core.types.json.serializer.EquipmentStateSerializer;
import com.github.vkremianskii.pits.core.types.json.serializer.MicrotypeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.TRUCK;

@Configuration
@Import({
    CoreExceptionHandler.class,
    RequestLoggingFilter.class})
public class CoreWebAutoConfiguration {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return objectMapperBuilder -> objectMapperBuilder
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .serializerByType(EquipmentId.class, new MicrotypeSerializer())
            .serializerByType(LocationId.class, new MicrotypeSerializer())
            .serializerByType(DozerState.class, new EquipmentStateSerializer())
            .serializerByType(DrillState.class, new EquipmentStateSerializer())
            .serializerByType(ShovelState.class, new EquipmentStateSerializer())
            .serializerByType(TruckState.class, new EquipmentStateSerializer())
            .deserializerByType(EquipmentId.class, new MicrotypeDeserializer<>(EquipmentId.class, UUID.class))
            .deserializerByType(Equipment.class, new EquipmentDeserializer())
            .deserializerByType(DozerState.class, new EquipmentStateDeserializer(DOZER))
            .deserializerByType(DrillState.class, new EquipmentStateDeserializer(DRILL))
            .deserializerByType(ShovelState.class, new EquipmentStateDeserializer(SHOVEL))
            .deserializerByType(TruckState.class, new EquipmentStateDeserializer(TRUCK))
            .deserializerByType(LocationId.class, new MicrotypeDeserializer<>(LocationId.class, UUID.class))
            .deserializerByType(Location.class, new LocationDeserializer())
            .build();
    }
}
