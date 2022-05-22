package com.github.vkremianskii.pits.processes.data;

import com.github.vkremianskii.pits.processes.model.HaulCycle;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class HaulCycleRepository {
    private static final Table<?> TABLE = table("haul_cycle");
    private static final Field<Long> FIELD_ID = field("id", Long.class);
    private static final Field<Integer> FIELD_TRUCK_ID = field("truck_id", Integer.class);
    private static final Field<Integer> FIELD_SHOVEL_ID = field("shovel_id", Integer.class);
    private static final Field<Timestamp> FIELD_WAIT_LOAD_TIMESTAMP = field("wait_load_timestamp", Timestamp.class);
    private static final Field<Timestamp> FIELD_START_LOAD_TIMESTAMP = field("start_load_timestamp", Timestamp.class);
    private static final Field<BigDecimal> FIELD_START_LOAD_LATITUDE = field("start_load_latitude", BigDecimal.class);
    private static final Field<BigDecimal> FIELD_START_LOAD_LONGITUDE = field("start_load_longitude", BigDecimal.class);
    private static final Field<Timestamp> FIELD_END_LOAD_TIMESTAMP = field("end_load_timestamp", Timestamp.class);
    private static final Field<Short> FIELD_END_LOAD_PAYLOAD = field("end_load_payload", Short.class);
    private static final Field<Timestamp> FIELD_START_UNLOAD_TIMESTAMP = field("start_unload_timestamp", Timestamp.class);
    private static final Field<Timestamp> FIELD_END_UNLOAD_TIMESTAMP = field("end_unload_timestamp", Timestamp.class);
    private static final Field<Timestamp> FIELD_INSERT_TIMESTAMP = field("insert_timestamp", Timestamp.class);

    private final DSLContext dslContext;

    public HaulCycleRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromCompletionStage(dslContext.deleteFrom(TABLE).executeAsync()).then();
    }

    public Mono<Void> put(int truckId,
                          @Nullable Integer shovelId,
                          @Nullable Instant waitLoadTimestamp,
                          @Nullable Instant startLoadTimestamp,
                          @Nullable Double startLoadLatitude,
                          @Nullable Double startLoadLongitude,
                          @Nullable Instant endLoadTimestamp,
                          @Nullable Integer endLoadPayload,
                          @Nullable Instant startUnloadTimestamp,
                          @Nullable Instant endUnloadTimestamp) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(
                        FIELD_TRUCK_ID,
                        FIELD_SHOVEL_ID,
                        FIELD_WAIT_LOAD_TIMESTAMP,
                        FIELD_START_LOAD_TIMESTAMP,
                        FIELD_START_LOAD_LATITUDE,
                        FIELD_START_LOAD_LONGITUDE,
                        FIELD_END_LOAD_TIMESTAMP,
                        FIELD_END_LOAD_PAYLOAD,
                        FIELD_START_UNLOAD_TIMESTAMP,
                        FIELD_END_UNLOAD_TIMESTAMP)
                .values(
                        truckId,
                        shovelId,
                        Optional.ofNullable(waitLoadTimestamp).map(Timestamp::from).orElse(null),
                        Optional.ofNullable(startLoadTimestamp).map(Timestamp::from).orElse(null),
                        Optional.ofNullable(startLoadLatitude).map(BigDecimal::valueOf).orElse(null),
                        Optional.ofNullable(startLoadLongitude).map(BigDecimal::valueOf).orElse(null),
                        Optional.ofNullable(endLoadTimestamp).map(Timestamp::from).orElse(null),
                        Optional.ofNullable(endLoadPayload).map(Integer::shortValue).orElse(null),
                        Optional.ofNullable(startUnloadTimestamp).map(Timestamp::from).orElse(null),
                        Optional.ofNullable(endUnloadTimestamp).map(Timestamp::from).orElse(null))
                .executeAsync()).then();
    }

    public Mono<Optional<HaulCycle>> getLastHaulCycleByTruckId(int truckId) {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE)
                .where(FIELD_TRUCK_ID.eq(truckId))
                .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                .fetchAsync()
                .thenApply(r -> r.map(HaulCycleRepository::haulCycleFromRecord))
                .thenApply(e -> !e.isEmpty() ? e.get(0) : null)
                .thenApply(Optional::ofNullable));
    }

    public Mono<Optional<HaulCycle>> getLastCompleteHaulCycleByTruckId(int truckId) {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE)
                .where(FIELD_TRUCK_ID.eq(truckId).and(FIELD_END_UNLOAD_TIMESTAMP.isNotNull()))
                .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                .fetchAsync()
                .thenApply(r -> r.map(HaulCycleRepository::haulCycleFromRecord))
                .thenApply(e -> !e.isEmpty() ? e.get(0) : null)
                .thenApply(Optional::ofNullable));
    }

    private static HaulCycle haulCycleFromRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var truckId = record.get(FIELD_TRUCK_ID);
        final var shovelId = record.get(FIELD_SHOVEL_ID);
        final var waitLoadTimestamp = Optional.ofNullable(record.get(FIELD_WAIT_LOAD_TIMESTAMP));
        final var startLoadTimestamp = Optional.ofNullable(record.get(FIELD_START_LOAD_TIMESTAMP));
        final var startLoadLatitude = Optional.ofNullable(record.get(FIELD_START_LOAD_LATITUDE));
        final var startLoadLongitude = Optional.ofNullable(record.get(FIELD_START_LOAD_LONGITUDE));
        final var endLoadTimestamp = Optional.ofNullable(record.get(FIELD_END_LOAD_TIMESTAMP));
        final var endLoadPayload = Optional.ofNullable(record.get(FIELD_END_LOAD_PAYLOAD));
        final var startUnloadTimestamp = Optional.ofNullable(record.get(FIELD_START_UNLOAD_TIMESTAMP));
        final var endUnloadTimestamp = Optional.ofNullable(record.get(FIELD_END_UNLOAD_TIMESTAMP));
        final var insertTimestamp = record.get(FIELD_INSERT_TIMESTAMP);

        return new HaulCycle(
                id,
                truckId,
                insertTimestamp.toInstant(),
                shovelId,
                waitLoadTimestamp.map(Timestamp::toInstant).orElse(null),
                startLoadTimestamp.map(Timestamp::toInstant).orElse(null),
                startLoadLatitude.map(BigDecimal::doubleValue).orElse(null),
                startLoadLongitude.map(BigDecimal::doubleValue).orElse(null),
                endLoadTimestamp.map(Timestamp::toInstant).orElse(null),
                endLoadPayload.map(Short::intValue).orElse(null),
                startUnloadTimestamp.map(Timestamp::toInstant).orElse(null),
                endUnloadTimestamp.map(Timestamp::toInstant).orElse(null));
    }
}
