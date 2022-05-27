package com.github.vkremianskii.pits.registry.types.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.*;

import java.io.IOException;

public class EquipmentDeserializer extends JsonDeserializer<Equipment> {

    @Override
    public Equipment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final var codec = p.getCodec();
        final JsonNode tree = codec.readTree(p);

        final var id = tree.get("id").asInt();
        final var name = tree.get("name").textValue();
        final var typeName = tree.get("type").textValue();
        final var stateName = tree.has("state") ? tree.get("state").textValue() : null;
        final var payload = tree.has("payload") ? tree.get("payload").asInt() : null;
        final var loadRadius = tree.has("loadRadius") ? tree.get("loadRadius").asInt() : 0;

        Position position = null;
        if (tree.has("position")) {
            final var positionNode = tree.get("position");
            position = new Position(
                positionNode.get("latitude").asDouble(),
                positionNode.get("longitude").asDouble(),
                positionNode.get("elevation").asInt());
        }

        final var type = EquipmentType.valueOf(typeName);
        return switch (type) {
            case DOZER -> new Dozer(
                id,
                name,
                stateName != null ? DozerState.valueOf(stateName) : null,
                position);
            case DRILL -> new Drill(
                id,
                name,
                stateName != null ? DrillState.valueOf(stateName) : null,
                position);
            case SHOVEL -> new Shovel(
                id,
                name,
                loadRadius,
                stateName != null ? ShovelState.valueOf(stateName) : null,
                position);
            case TRUCK -> new Truck(
                id,
                name,
                stateName != null ? TruckState.valueOf(stateName) : null,
                position,
                payload);
        };
    }
}