package com.github.vkremianskii.pits.registry.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.model.Location;
import com.github.vkremianskii.pits.registry.model.LocationId;
import com.github.vkremianskii.pits.registry.model.LocationType;
import com.github.vkremianskii.pits.registry.model.location.Dump;
import com.github.vkremianskii.pits.registry.model.location.Face;
import com.github.vkremianskii.pits.registry.model.location.Hole;
import com.github.vkremianskii.pits.registry.model.location.Stockpile;

import java.io.IOException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.StreamSupport.stream;

public class LocationDeserializer extends JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final var codec = p.getCodec();
        final JsonNode tree = codec.readTree(p);

        final var id = LocationId.valueOf(tree.get("id").textValue());
        final var name = tree.get("name").textValue();
        final var typeName = tree.get("type").textValue();

        final var geometry = Optional.ofNullable(tree.get("geometry"))
            .filter(JsonNode::isArray)
            .map(node -> stream(node.spliterator(), false)
                .map(LocationDeserializer::latLngPoint)
                .toList())
            .orElse(emptyList());

        final var type = LocationType.valueOf(typeName);
        return switch (type) {
            case DUMP -> new Dump(id, name, geometry);
            case FACE -> new Face(id, name, geometry);
            case HOLE -> new Hole(id, name, geometry);
            case STOCKPILE -> new Stockpile(id, name, geometry);
        };
    }

    private static LatLngPoint latLngPoint(JsonNode node) {
        final var latitude = node.get("latitude").doubleValue();
        final var longitude = node.get("longitude").doubleValue();
        return new LatLngPoint(latitude, longitude);
    }
}