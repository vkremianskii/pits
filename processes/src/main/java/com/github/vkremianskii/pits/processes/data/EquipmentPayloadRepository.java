package com.github.vkremianskii.pits.processes.data;

import com.github.vkremianskii.pits.core.data.TransactionalJooq;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.registry.model.EquipmentId;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.vkremianskii.pits.registry.model.EquipmentId.equipmentId;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class EquipmentPayloadRepository {

    private static final Table<?> TABLE = table("equipment_payload");
    private static final Field<Long> FIELD_ID = field("id", Long.class);
    private static final Field<UUID> FIELD_EQUIPMENT_ID = field("equipment_id", UUID.class);
    private static final Field<Integer> FIELD_PAYLOAD = field("payload", Integer.class);
    private static final Field<Timestamp> FIELD_INSERT_TIMESTAMP = field("insert_timestamp", Timestamp.class);

    private final TransactionalJooq transactionalJooq;

    public EquipmentPayloadRepository(TransactionalJooq transactionalJooq) {
        this.transactionalJooq = requireNonNull(transactionalJooq);
    }

    public Mono<Void> clear() {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.deleteFrom(TABLE)))
            .then();
    }

    public Mono<Void> insert(EquipmentId equipmentId, int payload, Instant insertTimestamp) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.insertInto(TABLE)
                .columns(FIELD_EQUIPMENT_ID, FIELD_PAYLOAD, FIELD_INSERT_TIMESTAMP)
                .values(equipmentId.value, payload, Timestamp.from(insertTimestamp))))
            .then();
    }

    public Mono<Optional<EquipmentPayloadRecord>> getLastRecordForEquipment(EquipmentId equipmentId) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.selectFrom(TABLE)
                .where(FIELD_EQUIPMENT_ID.eq(equipmentId.value))
                .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                .limit(1))
            .map(r -> r.map(EquipmentPayloadRepository::recordFromJooqRecord))
            .map(Optional::of)
            .switchIfEmpty(Mono.just(Optional.empty())));
    }

    public Mono<Optional<EquipmentPayloadRecord>> getLastRecordForEquipmentBefore(EquipmentId equipmentId, Instant timestamp) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.selectFrom(TABLE)
                .where(FIELD_EQUIPMENT_ID.eq(equipmentId.value).and(FIELD_INSERT_TIMESTAMP.le(Timestamp.from(timestamp))))
                .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                .limit(1))
            .map(r -> r.map(EquipmentPayloadRepository::recordFromJooqRecord))
            .map(Optional::of)
            .switchIfEmpty(Mono.just(Optional.empty())));
    }

    public Mono<List<EquipmentPayloadRecord>> getRecordsForEquipmentAfter(EquipmentId equipmentId, Instant timestamp) {
        return transactionalJooq.inTransactionalContext(ctx -> Flux.from(ctx.selectFrom(TABLE)
                .where(FIELD_EQUIPMENT_ID.eq(equipmentId.value).and(FIELD_INSERT_TIMESTAMP.gt(Timestamp.from(timestamp))))
                .orderBy(FIELD_INSERT_TIMESTAMP))
            .map(r -> r.map(EquipmentPayloadRepository::recordFromJooqRecord))
            .collectList());
    }

    private static EquipmentPayloadRecord recordFromJooqRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var equipmentId = equipmentId(record.get(FIELD_EQUIPMENT_ID));
        final var payload = record.get(FIELD_PAYLOAD);
        final var insertTimestamp = record.get(FIELD_INSERT_TIMESTAMP);

        return new EquipmentPayloadRecord(
            id,
            equipmentId,
            payload,
            insertTimestamp.toInstant());
    }
}
