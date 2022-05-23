package com.github.vkremianskii.pits.registry.types.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;

import java.io.IOException;

public class EquipmentStateSerializer extends JsonSerializer<EquipmentState> {

    @Override
    public void serialize(EquipmentState value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.name());
    }
}
