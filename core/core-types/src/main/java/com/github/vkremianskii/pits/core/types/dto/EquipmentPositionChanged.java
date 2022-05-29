package com.github.vkremianskii.pits.core.types.dto;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;

public record EquipmentPositionChanged(EquipmentId equipmentId,
                                       double latitude,
                                       double longitude,
                                       int elevation) {

}
