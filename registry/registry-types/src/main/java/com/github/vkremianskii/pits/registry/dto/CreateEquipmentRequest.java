package com.github.vkremianskii.pits.registry.dto;

import com.github.vkremianskii.pits.core.model.EquipmentType;

public record CreateEquipmentRequest(String name, EquipmentType type) {

}
