package com.github.vkremianskii.pits.core.types.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.vkremianskii.pits.core.types.Microtype;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class MicrotypeDeserializer<T extends Microtype<V>, V extends Comparable<V>> extends JsonDeserializer<T> {

    private final Class<T> typeClass;
    private final Class<V> valueClass;

    public MicrotypeDeserializer(Class<T> typeClass, Class<V> valueClass) {
        this.typeClass = requireNonNull(typeClass);
        this.valueClass = requireNonNull(valueClass);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Comparable<?> value = p.readValueAs(valueClass);
        try {
            final var cls = typeClass.getDeclaredConstructor(valueClass);
            cls.setAccessible(true);
            return cls.newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
