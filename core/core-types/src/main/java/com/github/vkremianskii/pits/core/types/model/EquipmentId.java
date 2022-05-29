package com.github.vkremianskii.pits.core.types.model;

import com.github.vkremianskii.pits.core.types.Microtype;

import java.util.UUID;

public class EquipmentId extends Microtype<UUID> {

    private EquipmentId(UUID value) {
        super(value);
    }

    public static EquipmentId equipmentId(UUID value) {
        return new EquipmentId(value);
    }

    public static EquipmentId valueOf(String value) {
        return equipmentId(UUID.fromString(value));
    }
}
