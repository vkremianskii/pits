package com.github.vkremianskii.pits.core.dto;

import com.github.vkremianskii.pits.core.model.EquipmentId;

public record EquipmentPositionChanged(EquipmentId equipmentId,
                                       double latitude,
                                       double longitude,
                                       int elevation) {

}
