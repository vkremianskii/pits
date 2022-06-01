package com.github.vkremianskii.pits.registry.data;

import com.github.vkremianskii.pits.core.data.TransactionalJooq;
import com.github.vkremianskii.pits.registry.model.LocationId;
import com.github.vkremianskii.pits.registry.model.LocationPoint;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.github.vkremianskii.pits.registry.model.LocationId.locationId;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class LocationPointRepository {

    private static final Table<?> TABLE = table("location_point");
    private static final Field<Integer> FIELD_ID = field("id", Integer.class);
    private static final Field<UUID> FIELD_LOCATION_ID = field("location_id", UUID.class);
    private static final Field<Short> FIELD_POINT_ORDER = field("point_order", Short.class);
    private static final Field<Double> FIELD_LATITUDE = field("latitude", Double.class);
    private static final Field<Double> FIELD_LONGITUDE = field("longitude", Double.class);

    private final TransactionalJooq transactionalJooq;

    public LocationPointRepository(TransactionalJooq transactionalJooq) {
        this.transactionalJooq = requireNonNull(transactionalJooq);
    }

    public Mono<Void> clear() {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.deleteFrom(TABLE)))
            .then();
    }

    public Mono<List<LocationPoint>> getPointsByLocationId(LocationId locationId) {
        return transactionalJooq.inTransactionalContext(ctx -> Flux.from(ctx.selectFrom(TABLE)
                .where(FIELD_LOCATION_ID.eq(locationId.value))
                .orderBy(FIELD_POINT_ORDER))
            .map(r -> r.map(LocationPointRepository::locationPointFromRecord))
            .collectList());
    }

    public Mono<Void> createLocationPoint(LocationId locationId,
                                          int order,
                                          double latitude,
                                          double longitude) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.insertInto(TABLE)
                .columns(FIELD_LOCATION_ID, FIELD_POINT_ORDER, FIELD_LATITUDE, FIELD_LONGITUDE)
                .values(
                    locationId.value,
                    (short) order,
                    latitude,
                    longitude)))
            .then();
    }

    private static LocationPoint locationPointFromRecord(org.jooq.Record record) {
        return new LocationPoint(
            record.get(FIELD_ID),
            locationId(record.get(FIELD_LOCATION_ID)),
            record.get(FIELD_POINT_ORDER).intValue(),
            record.get(FIELD_LATITUDE),
            record.get(FIELD_LONGITUDE));
    }
}
