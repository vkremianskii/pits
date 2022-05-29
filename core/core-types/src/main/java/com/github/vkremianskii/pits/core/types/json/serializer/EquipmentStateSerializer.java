package com.github.vkremianskii.pits.core.types.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.vkremianskii.pits.core.types.model.EquipmentState;

import java.io.IOException;

public class EquipmentStateSerializer extends StdSerializer<EquipmentState> {

    public EquipmentStateSerializer() {
        super(EquipmentState.class);
    }

    @Override
    public void serialize(EquipmentState value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.name);
    }
}
