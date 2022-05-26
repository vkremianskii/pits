package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;

public record EquipmentPositionRecord(long id,
                                      int equipmentId,
                                      double latitude,
                                      double longitude,
                                      int elevation,
                                      Instant insertTimestamp) {
}
