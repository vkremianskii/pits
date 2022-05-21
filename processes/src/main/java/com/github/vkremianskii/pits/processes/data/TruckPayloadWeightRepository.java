package com.github.vkremianskii.pits.processes.data;

import com.github.vkremianskii.pits.processes.model.TruckPayloadWeightRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class TruckPayloadWeightRepository {
    private static final Table<?> TABLE = table("truck_payload_weight");
    private static final Field<Long> FIELD_ID = field("id", Long.class);
    private static final Field<Integer> FIELD_EQUIPMENT_ID = field("equipment_id", Integer.class);
    private static final Field<Integer> FIELD_WEIGHT = field("weight", Integer.class);
    private static final Field<Timestamp> FIELD_INSERT_TIMESTAMP = field("insert_timestamp", Timestamp.class);

    private final DSLContext dslContext;

    public TruckPayloadWeightRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromCompletionStage(dslContext.deleteFrom(TABLE).executeAsync()).then();
    }

    public Mono<Void> put(int equipmentId, int weight) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_EQUIPMENT_ID, FIELD_WEIGHT)
                .values(equipmentId, weight)
                .executeAsync()).then();
    }

    public Mono<Optional<TruckPayloadWeightRecord>> getLastRecordByEquipmentId(int equipmentId) {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE)
                .where(FIELD_EQUIPMENT_ID.eq(equipmentId))
                .fetchAsync()
                .thenApply(r -> r.map(TruckPayloadWeightRepository::recordFromJooqRecord))
                .thenApply(e -> !e.isEmpty() ? e.get(0) : null)
                .thenApply(Optional::ofNullable));
    }

    private static TruckPayloadWeightRecord recordFromJooqRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var equipmentId = record.get(FIELD_EQUIPMENT_ID);
        final var weight = record.get(FIELD_WEIGHT);
        final var insertTimestamp = record.get(FIELD_INSERT_TIMESTAMP);

        return new TruckPayloadWeightRecord(
                id,
                equipmentId,
                weight,
                insertTimestamp.toInstant());
    }
}
