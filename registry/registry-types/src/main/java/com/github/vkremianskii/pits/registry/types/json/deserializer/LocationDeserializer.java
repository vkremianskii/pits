package com.github.vkremianskii.pits.registry.types.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.vkremianskii.pits.registry.types.model.Location;
import com.github.vkremianskii.pits.registry.types.model.LocationType;
import com.github.vkremianskii.pits.registry.types.model.location.Dump;
import com.github.vkremianskii.pits.registry.types.model.location.Face;
import com.github.vkremianskii.pits.registry.types.model.location.Hole;
import com.github.vkremianskii.pits.registry.types.model.location.Stockpile;

import java.io.IOException;

public class LocationDeserializer extends JsonDeserializer<Location> {

        @Override
        public Location deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            final var codec = p.getCodec();
            final JsonNode tree = codec.readTree(p);

            final var id = tree.get("id").asInt();
            final var name = tree.get("name").textValue();
            final var typeName = tree.get("type").textValue();

            final var type = LocationType.valueOf(typeName);
            return switch (type) {
                case DUMP -> new Dump(id, name);
                case FACE -> new Face(id, name);
                case HOLE -> new Hole(id, name);
                case STOCKPILE -> new Stockpile(id, name);
            };
        }
    }