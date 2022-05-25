package com.github.vkremianskii.pits.processes.data;

import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static reactor.core.scheduler.Schedulers.boundedElastic;

@Repository
public class EquipmentPositionRepository {
    private static final Table<?> TABLE = table("equipment_position");
    private static final Field<Long> FIELD_ID = field("id", Long.class);
    private static final Field<Integer> FIELD_EQUIPMENT_ID = field("equipment_id", Integer.class);
    private static final Field<BigDecimal> FIELD_LATITUDE = field("latitude", BigDecimal.class);
    private static final Field<BigDecimal> FIELD_LONGITUDE = field("longitude", BigDecimal.class);
    private static final Field<Integer> FIELD_ELEVATION = field("elevation", Integer.class);
    private static final Field<Timestamp> FIELD_INSERT_TIMESTAMP = field("insert_timestamp", Timestamp.class);

    private final DSLContext dslContext;

    public EquipmentPositionRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromRunnable(() -> dslContext.deleteFrom(TABLE).execute());
    }

    public Mono<Void> insert(int equipmentId,
                             double latitude,
                             double longitude,
                             int elevation) {
        return Mono.<Void>fromRunnable(() -> dslContext.insertInto(TABLE)
                        .columns(FIELD_EQUIPMENT_ID, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_ELEVATION)
                        .values(
                                equipmentId,
                                BigDecimal.valueOf(latitude),
                                BigDecimal.valueOf(longitude),
                                elevation)
                        .execute())
                .subscribeOn(boundedElastic());
    }

    public Mono<Void> insert(int equipmentId,
                             double latitude,
                             double longitude,
                             int elevation,
                             Instant insertTimestamp) {
        return Mono.<Void>fromRunnable(() -> dslContext.insertInto(TABLE)
                        .columns(FIELD_EQUIPMENT_ID, FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_ELEVATION, FIELD_INSERT_TIMESTAMP)
                        .values(
                                equipmentId,
                                BigDecimal.valueOf(latitude),
                                BigDecimal.valueOf(longitude),
                                elevation,
                                Timestamp.from(insertTimestamp))
                        .execute())
                .subscribeOn(boundedElastic());
    }

    public Mono<Optional<EquipmentPositionRecord>> getLastRecordForEquipment(int equipmentId) {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
                        .where(FIELD_EQUIPMENT_ID.eq(equipmentId))
                        .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                        .limit(1)
                        .fetchOptional(r -> r.map(EquipmentPositionRepository::recordFromJooqRecord)))
                .subscribeOn(boundedElastic());
    }

    public Mono<Optional<EquipmentPositionRecord>> getLastRecordForEquipmentBefore(int equipmentId, Instant timestamp) {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
                        .where(FIELD_EQUIPMENT_ID.eq(equipmentId).and(FIELD_INSERT_TIMESTAMP.le(Timestamp.from(timestamp))))
                        .orderBy(FIELD_INSERT_TIMESTAMP.desc())
                        .limit(1)
                        .fetchOptional(r -> r.map(EquipmentPositionRepository::recordFromJooqRecord)))
                .subscribeOn(boundedElastic());
    }

    public Mono<List<EquipmentPositionRecord>> getRecordsForEquipmentAfter(int equipmentId, Instant timestamp) {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
                        .where(FIELD_EQUIPMENT_ID.eq(equipmentId).and(FIELD_INSERT_TIMESTAMP.gt(Timestamp.from(timestamp))))
                        .orderBy(FIELD_INSERT_TIMESTAMP)
                        .fetch(r -> r.map(EquipmentPositionRepository::recordFromJooqRecord)))
                .subscribeOn(boundedElastic());
    }

    private static EquipmentPositionRecord recordFromJooqRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var equipmentId = record.get(FIELD_EQUIPMENT_ID);
        final var latitude = record.get(FIELD_LATITUDE);
        final var longitude = record.get(FIELD_LONGITUDE);
        final var elevation = record.get(FIELD_ELEVATION);
        final var insertTimestamp = record.get(FIELD_INSERT_TIMESTAMP);

        return new EquipmentPositionRecord(
                id,
                equipmentId,
                latitude.doubleValue(),
                longitude.doubleValue(),
                elevation,
                insertTimestamp.toInstant());
    }
}
