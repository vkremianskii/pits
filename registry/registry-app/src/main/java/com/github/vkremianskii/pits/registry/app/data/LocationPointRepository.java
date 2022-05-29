package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.core.types.model.LocationId;
import com.github.vkremianskii.pits.core.types.model.LocationPoint;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.LocationId.locationId;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class LocationPointRepository {

    private static final Table<?> TABLE = table("location_point");
    private static final Field<Integer> FIELD_ID = field("id", Integer.class);
    private static final Field<UUID> FIELD_LOCATION_ID = field("location_id", UUID.class);
    private static final Field<Short> FIELD_POINT_ORDER = field("point_order", Short.class);
    private static final Field<BigDecimal> FIELD_LATITUDE = field("latitude", BigDecimal.class);
    private static final Field<BigDecimal> FIELD_LONGITUDE = field("longitude", BigDecimal.class);
    private final DSLContext dslContext;

    public LocationPointRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.from(dslContext.deleteFrom(TABLE)).then();
    }

    public Mono<List<LocationPoint>> getPointsByLocationId(LocationId locationId) {
        return Flux.from(dslContext.selectFrom(TABLE)
                .where(FIELD_LOCATION_ID.eq(locationId.value))
                .orderBy(FIELD_POINT_ORDER))
            .map(r -> r.map(LocationPointRepository::locationPointFromRecord))
            .collectList();
    }

    public Mono<Void> createLocationPoint(LocationId locationId,
                                          int order,
                                          double latitude,
                                          double longitude) {
        return Mono.from(dslContext.insertInto(TABLE)
                .columns(FIELD_LOCATION_ID, FIELD_POINT_ORDER, FIELD_LATITUDE, FIELD_LONGITUDE)
                .values(locationId.value, (short) order, BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude)))
            .then();
    }

    private static LocationPoint locationPointFromRecord(org.jooq.Record record) {
        return new LocationPoint(
            record.get(FIELD_ID),
            locationId(record.get(FIELD_LOCATION_ID)),
            record.get(FIELD_POINT_ORDER).intValue(),
            record.get(FIELD_LATITUDE).doubleValue(),
            record.get(FIELD_LONGITUDE).doubleValue());
    }
}
