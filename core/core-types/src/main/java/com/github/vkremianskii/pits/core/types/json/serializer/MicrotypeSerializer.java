package com.github.vkremianskii.pits.core.types.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.vkremianskii.pits.core.types.Microtype;

import java.io.IOException;

public class MicrotypeSerializer extends StdSerializer<Microtype<?>> {

    public MicrotypeSerializer() {
        super(SimpleType.constructUnsafe(Microtype.class));
    }

    @Override
    public void serialize(Microtype value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
