package com.github.vkremianskii.pits.core.types.dto;

import java.util.UUID;

public record EquipmentPositionChanged(UUID equipmentId,
                                       double latitude,
                                       double longitude,
                                       int elevation) {

}
