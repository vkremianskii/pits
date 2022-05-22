package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class EquipmentPayloadRecord {
    private final long id;
    private final int equipmentId;
    private final int payload;
    private final Instant insertTimestamp;

    public EquipmentPayloadRecord(long id,
                                  int equipmentId,
                                  int payload,
                                  Instant insertTimestamp) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.payload = payload;
        this.insertTimestamp = requireNonNull(insertTimestamp);
    }

    public long id() {
        return id;
    }

    public int equipmentId() {
        return equipmentId;
    }

    public double payload() {
        return payload;
    }

    public Instant insertTimestamp() {
        return insertTimestamp;
    }
}
