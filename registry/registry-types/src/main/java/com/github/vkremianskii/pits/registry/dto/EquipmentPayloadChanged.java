package com.github.vkremianskii.pits.registry.dto;

import com.github.vkremianskii.pits.registry.model.EquipmentId;

public record EquipmentPayloadChanged(EquipmentId equipmentId, int payload) {

}
