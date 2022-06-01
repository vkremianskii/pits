package com.github.vkremianskii.pits.registry.dto;

import com.github.vkremianskii.pits.registry.model.EquipmentId;

public record EquipmentPositionChanged(EquipmentId equipmentId,
                                       double latitude,
                                       double longitude,
                                       int elevation) {

}
