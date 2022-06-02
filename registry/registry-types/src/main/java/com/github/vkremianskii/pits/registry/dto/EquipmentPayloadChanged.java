package com.github.vkremianskii.pits.registry.dto;

import com.github.vkremianskii.pits.registry.model.EquipmentId;

import java.time.Instant;

public record EquipmentPayloadChanged(EquipmentId equipmentId,
                                      int payload,
                                      Instant receiveTimestamp) {

}
