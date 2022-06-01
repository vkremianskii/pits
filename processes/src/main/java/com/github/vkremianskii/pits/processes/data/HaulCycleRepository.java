package com.github.vkremianskii.pits.processes.data;

import com.github.vkremianskii.pits.core.data.TransactionalJooq;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.registry.model.EquipmentId;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.github.vkremianskii.pits.registry.model.EquipmentId.equipmentId;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class HaulCycleRepository {

    private static final Table<?> TABLE = table("haul_cycle");
    private static final Field<Long> FIELD_ID = field("id", Long.class);
    private static final Field<UUID> FIELD_TRUCK_ID = field("truck_id", UUID.class);
    private static final Field<UUID> FIELD_SHOVEL_ID = field("shovel_id", UUID.class);
    private static final Field<Timestamp> FIELD_WAIT_LOAD_TIMESTAMP = field("wait_load_timestamp", Timestamp.class);
    private static final Field<Timestamp> FIELD_START_LOAD_TIMESTAMP = field("start_load_timestamp", Timestamp.class);
    private static final Field<Double> FIELD_START_LOAD_LATITUDE = field("start_load_latitude", Double.class);
    private static final Field<Double> FIELD_START_LOAD_LONGITUDE = field("start_load_longitude", Double.class);
    private static final Field<Timestamp> FIELD_END_LOAD_TIMESTAMP = field("end_load_timestamp", Timestamp.class);
    private static final Field<Integer> FIELD_END_LOAD_PAYLOAD = field("end_load_payload", Integer.class);
    private static final Field<Timestamp> FIELD_START_UNLOAD_TIMESTAMP = field("start_unload_timestamp", Timestamp.class);
    private static final Field<Timestamp> FIELD_END_UNLOAD_TIMESTAMP = field("end_unload_timestamp", Timestamp.class);
    private static final Field<Timestamp> FIELD_INSERT_TIMESTAMP = field("insert_timestamp", Timestamp.class);

    private final TransactionalJooq transactionalJooq;

    public HaulCycleRepository(TransactionalJooq transactionalJooq) {
        this.transactionalJooq = requireNonNull(transactionalJooq);
    }

    public Mono<Void> clear() {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.deleteFrom(TABLE)))
            .then();
    }

    public Mono<Void> insert(EquipmentId truckId,
                             @Nullable EquipmentId shovelId,
                             @Nullable Instant waitLoadTimestamp,
                             @Nullable Instant startLoadTimestamp,
                             @Nullable Double startLoadLatitude,
                             @Nullable Double startLoadLongitude,
                             @Nullable Instant endLoadTimestamp,
                             @Nullable Integer endLoadPayload,
                             @Nullable Instant startUnloadTimestamp,
                             @Nullable Instant endUnloadTimestamp) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.insertInto(TABLE)
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
                    truckId.value,
                    shovelId != null ? shovelId.value : null,
                    Optional.ofNullable(waitLoadTimestamp).map(Timestamp::from).orElse(null),
                    Optional.ofNullable(startLoadTimestamp).map(Timestamp::from).orElse(null),
                    startLoadLatitude,
                    startLoadLongitude,
                    Optional.ofNullable(endLoadTimestamp).map(Timestamp::from).orElse(null),
                    endLoadPayload,
                    Optional.ofNullable(startUnloadTimestamp).map(Timestamp::from).orElse(null),
                    Optional.ofNullable(endUnloadTimestamp).map(Timestamp::from).orElse(null))))
            .then();
    }

    public Mono<Void> update(long haulCycleId,
                             @Nullable EquipmentId shovelId,
                             @Nullable Instant waitLoadTimestamp,
                             @Nullable Instant startLoadTimestamp,
                             @Nullable Double startLoadLatitude,
                             @Nullable Double startLoadLongitude,
                             @Nullable Instant endLoadTimestamp,
                             @Nullable Integer endLoadPayload,
                             @Nullable Instant startUnloadTimestamp,
                             @Nullable Instant endUnloadTimestamp) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.update(TABLE)
                .set(FIELD_SHOVEL_ID, shovelId != null ? shovelId.value : null)
                .set(FIELD_WAIT_LOAD_TIMESTAMP, Optional.ofNullable(waitLoadTimestamp).map(Timestamp::from).orElse(null))
                .set(FIELD_START_LOAD_TIMESTAMP, Optional.ofNullable(startLoadTimestamp).map(Timestamp::from).orElse(null))
                .set(FIELD_START_LOAD_LATITUDE, startLoadLatitude)
                .set(FIELD_START_LOAD_LONGITUDE, startLoadLongitude)
                .set(FIELD_END_LOAD_TIMESTAMP, Optional.ofNullable(endLoadTimestamp).map(Timestamp::from).orElse(null))
                .set(FIELD_END_LOAD_PAYLOAD, endLoadPayload)
                .set(FIELD_START_UNLOAD_TIMESTAMP, Optional.ofNullable(startUnloadTimestamp).map(Timestamp::from).orElse(null))
                .set(FIELD_END_UNLOAD_TIMESTAMP, Optional.ofNullable(endUnloadTimestamp).map(Timestamp::from).orElse(null))
                .where(FIELD_ID.eq(haulCycleId))))
            .then();
    }

    public Mono<Optional<HaulCycle>> getLastHaulCycleForTruck(EquipmentId truckId) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.selectFrom(TABLE)
                .where(FIELD_TRUCK_ID.eq(truckId.value))
                .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                .limit(1))
            .map(r -> r.map(HaulCycleRepository::haulCycleFromRecord))
            .map(Optional::of)
            .switchIfEmpty(Mono.just(Optional.empty())));
    }

    private static HaulCycle haulCycleFromRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var truckId = equipmentId(record.get(FIELD_TRUCK_ID));
        final var shovelId = Optional.ofNullable(record.get(FIELD_SHOVEL_ID))
            .map(EquipmentId::equipmentId)
            .orElse(null);
        final var waitLoadTimestamp = Optional.ofNullable(record.get(FIELD_WAIT_LOAD_TIMESTAMP));
        final var startLoadTimestamp = Optional.ofNullable(record.get(FIELD_START_LOAD_TIMESTAMP));
        final var startLoadLatitude = record.get(FIELD_START_LOAD_LATITUDE);
        final var startLoadLongitude = record.get(FIELD_START_LOAD_LONGITUDE);
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
            startLoadLatitude,
            startLoadLongitude,
            endLoadTimestamp.map(Timestamp::toInstant).orElse(null),
            endLoadPayload.orElse(null),
            startUnloadTimestamp.map(Timestamp::toInstant).orElse(null),
            endUnloadTimestamp.map(Timestamp::toInstant).orElse(null));
    }
}
