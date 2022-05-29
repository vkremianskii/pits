package com.github.vkremianskii.pits.processes.model;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;

import java.time.Instant;

public record EquipmentPositionRecord(long id,
                                      EquipmentId equipmentId,
                                      double latitude,
                                      double longitude,
                                      int elevation,
                                      Instant insertTimestamp) {

}
