package com.github.vkremianskii.pits.core.types.dto;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;

public record EquipmentPayloadChanged(EquipmentId equipmentId, int payload) {

}
