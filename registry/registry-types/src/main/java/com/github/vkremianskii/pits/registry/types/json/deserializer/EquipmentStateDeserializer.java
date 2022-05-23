package com.github.vkremianskii.pits.registry.types.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.equipment.DozerState;
import com.github.vkremianskii.pits.registry.types.model.equipment.DrillState;
import com.github.vkremianskii.pits.registry.types.model.equipment.ShovelState;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class EquipmentStateDeserializer extends JsonDeserializer<EquipmentState> {
    private final EquipmentType equipmentType;

    public EquipmentStateDeserializer(EquipmentType equipmentType) {
        this.equipmentType = requireNonNull(equipmentType);
    }

    @Override
    public EquipmentState deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final var name = p.getText();
        return switch (equipmentType) {
            case DOZER -> DozerState.valueOf(name);
            case DRILL -> DrillState.valueOf(name);
            case SHOVEL -> ShovelState.valueOf(name);
            case TRUCK -> TruckState.valueOf(name);
        };
    }
}
