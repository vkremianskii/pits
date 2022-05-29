package com.github.vkremianskii.pits.core.types.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.vkremianskii.pits.core.types.Microtype;

import java.io.IOException;

public class MicrotypeSerializer extends JsonSerializer<Microtype<?>> {

    @Override
    public void serialize(Microtype<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
