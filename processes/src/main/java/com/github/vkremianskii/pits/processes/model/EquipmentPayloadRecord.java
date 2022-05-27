package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;

public record EquipmentPayloadRecord(long id,
                                     int equipmentId,
                                     int payload,
                                     Instant insertTimestamp) {

}
