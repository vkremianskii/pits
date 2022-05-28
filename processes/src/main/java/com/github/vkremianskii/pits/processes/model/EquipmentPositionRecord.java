package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;
import java.util.UUID;

public record EquipmentPositionRecord(long id,
                                      UUID equipmentId,
                                      double latitude,
                                      double longitude,
                                      int elevation,
                                      Instant insertTimestamp) {

}
