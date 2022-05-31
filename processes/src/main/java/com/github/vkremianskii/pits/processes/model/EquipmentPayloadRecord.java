package com.github.vkremianskii.pits.processes.model;

import com.github.vkremianskii.pits.core.model.EquipmentId;

import java.time.Instant;

public record EquipmentPayloadRecord(long id,
                                     EquipmentId equipmentId,
                                     int payload,
                                     Instant insertTimestamp) {

}
