package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;
import java.util.UUID;

public record EquipmentPayloadRecord(long id,
                                     UUID equipmentId,
                                     int payload,
                                     Instant insertTimestamp) {

}
