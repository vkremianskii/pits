package com.github.vkremianskii.pits.processes.data;

import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class EquipmentPayloadRepository {
    private static final Table<?> TABLE = table("equipment_payload");
    private static final Field<Long> FIELD_ID = field("id", Long.class);
    private static final Field<Integer> FIELD_EQUIPMENT_ID = field("equipment_id", Integer.class);
    private static final Field<Integer> FIELD_PAYLOAD = field("payload", Integer.class);
    private static final Field<Timestamp> FIELD_INSERT_TIMESTAMP = field("insert_timestamp", Timestamp.class);

    private final DSLContext dslContext;

    public EquipmentPayloadRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromCompletionStage(dslContext.deleteFrom(TABLE).executeAsync()).then();
    }

    public Mono<Void> insert(int equipmentId, int payload) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_EQUIPMENT_ID, FIELD_PAYLOAD)
                .values(equipmentId, payload)
                .executeAsync()).then();
    }

    public Mono<Void> insert(int equipmentId, int payload, Instant insertTimestamp) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_EQUIPMENT_ID, FIELD_PAYLOAD, FIELD_INSERT_TIMESTAMP)
                .values(equipmentId, payload, Timestamp.from(insertTimestamp))
                .executeAsync()).then();
    }

    public Mono<Optional<EquipmentPayloadRecord>> getLastRecordForEquipment(int equipmentId) {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE)
                .where(FIELD_EQUIPMENT_ID.eq(equipmentId))
                .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                .fetchAsync()
                .thenApply(r -> r.map(EquipmentPayloadRepository::recordFromJooqRecord))
                .thenApply(e -> !e.isEmpty() ? e.get(0) : null)
                .thenApply(Optional::ofNullable));
    }

    public Mono<Optional<EquipmentPayloadRecord>> getLastRecordForEquipmentBefore(int equipmentId, Instant timestamp) {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE)
                .where(FIELD_EQUIPMENT_ID.eq(equipmentId).and(FIELD_INSERT_TIMESTAMP.le(Timestamp.from(timestamp))))
                .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                .fetchAsync()
                .thenApply(r -> r.map(EquipmentPayloadRepository::recordFromJooqRecord))
                .thenApply(e -> !e.isEmpty() ? e.get(0) : null)
                .thenApply(Optional::ofNullable));
    }

    public Mono<List<EquipmentPayloadRecord>> getRecordsForEquipmentAfter(int equipmentId, Instant timestamp) {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE)
                .where(FIELD_EQUIPMENT_ID.eq(equipmentId).and(FIELD_INSERT_TIMESTAMP.gt(Timestamp.from(timestamp))))
                .orderBy(FIELD_INSERT_TIMESTAMP)
                .fetchAsync()
                .thenApply(r -> r.map(EquipmentPayloadRepository::recordFromJooqRecord)));
    }

    private static EquipmentPayloadRecord recordFromJooqRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var equipmentId = record.get(FIELD_EQUIPMENT_ID);
        final var payload = record.get(FIELD_PAYLOAD);
        final var insertTimestamp = record.get(FIELD_INSERT_TIMESTAMP);

        return new EquipmentPayloadRecord(
                id,
                equipmentId,
                payload,
                insertTimestamp.toInstant());
    }
}
