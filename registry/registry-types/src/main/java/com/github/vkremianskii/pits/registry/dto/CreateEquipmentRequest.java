package com.github.vkremianskii.pits.registry.dto;

import com.github.vkremianskii.pits.registry.model.EquipmentType;

public record CreateEquipmentRequest(String name, EquipmentType type) {

}
