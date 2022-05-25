package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;
import java.util.Objects;

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

    public int payload() {
        return payload;
    }

    public Instant insertTimestamp() {
        return insertTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPayloadRecord that = (EquipmentPayloadRecord) o;
        return id == that.id && equipmentId == that.equipmentId && payload == that.payload && Objects.equals(insertTimestamp, that.insertTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, equipmentId, payload, insertTimestamp);
    }
}
