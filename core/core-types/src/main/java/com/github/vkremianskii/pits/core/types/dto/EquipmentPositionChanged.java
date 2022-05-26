package com.github.vkremianskii.pits.core.types.dto;

public record EquipmentPositionChanged(int equipmentId,
                                       double latitude,
                                       double longitude,
                                       int elevation) {
}
