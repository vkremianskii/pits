package com.github.vkremianskii.pits.core.dto;

import com.github.vkremianskii.pits.core.model.EquipmentId;

public record EquipmentPayloadChanged(EquipmentId equipmentId, int payload) {

}
