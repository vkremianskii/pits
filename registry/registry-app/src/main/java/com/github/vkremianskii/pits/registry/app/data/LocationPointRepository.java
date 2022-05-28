package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.types.model.LocationPoint;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static reactor.core.scheduler.Schedulers.boundedElastic;

@Repository
public class LocationPointRepository {

    private static final Table<?> TABLE = table("location_point");
    private static final Field<Integer> FIELD_ID = field("id", Integer.class);
    private static final Field<Integer> FIELD_LOCATION_ID = field("location_id", Integer.class);
    private static final Field<Short> FIELD_POINT_ORDER = field("point_order", Short.class);
    private static final Field<BigDecimal> FIELD_LATITUDE = field("latitude", BigDecimal.class);
    private static final Field<BigDecimal> FIELD_LONGITUDE = field("longitude", BigDecimal.class);
    private final DSLContext dslContext;

    public LocationPointRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.<Void>fromRunnable(() -> dslContext.deleteFrom(TABLE).execute())
            .subscribeOn(boundedElastic());
    }

    public Mono<Void> insert(int locationId, int order, double latitude, double longitude) {
        return Mono.<Void>fromRunnable(() -> dslContext.insertInto(TABLE)
                .columns(FIELD_LOCATION_ID, FIELD_POINT_ORDER, FIELD_LATITUDE, FIELD_LONGITUDE)
                .values(locationId, (short) order, BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude))
                .execute())
            .subscribeOn(boundedElastic());
    }

    public Mono<List<LocationPoint>> getPointsByLocationId(int locationId) {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
                .where(FIELD_LOCATION_ID.eq(locationId))
                .fetch(r -> r.map(LocationPointRepository::locationPointFromRecord)))
            .subscribeOn(boundedElastic());
    }

    private static LocationPoint locationPointFromRecord(org.jooq.Record record) {
        return new LocationPoint(
            record.get(FIELD_ID),
            record.get(FIELD_LOCATION_ID),
            record.get(FIELD_POINT_ORDER).intValue(),
            record.get(FIELD_LATITUDE).doubleValue(),
            record.get(FIELD_LONGITUDE).doubleValue());
    }
}
