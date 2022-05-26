package com.github.vkremianskii.pits.registry.types.dto;

import com.github.vkremianskii.pits.registry.types.model.EquipmentType;

public record CreateEquipmentRequest(String name, EquipmentType type) {
}
