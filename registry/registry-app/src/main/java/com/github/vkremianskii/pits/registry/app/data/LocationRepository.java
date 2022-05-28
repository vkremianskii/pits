package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.core.web.error.InternalServerError;
import com.github.vkremianskii.pits.registry.types.model.LocationDeclaration;
import com.github.vkremianskii.pits.registry.types.model.LocationType;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.STOCKPILE;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static reactor.core.scheduler.Schedulers.boundedElastic;

@Repository
public class LocationRepository {

    private static final Table<?> TABLE = table("location");
    private static final Field<Integer> FIELD_ID = field("id", Integer.class);
    private static final Field<String> FIELD_NAME = field("name", String.class);
    private static final Field<String> FIELD_TYPE = field("type", String.class);

    private static final Map<LocationType, String> TYPE_TO_VALUE = Map.of(
        DUMP, "dump",
        FACE, "face",
        HOLE, "hole",
        STOCKPILE, "stockpile");

    private final DSLContext dslContext;

    public LocationRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.<Void>fromRunnable(() -> dslContext.deleteFrom(TABLE).execute())
            .subscribeOn(boundedElastic());
    }

    public Mono<Integer> insert(String name, LocationType type) {
        return Mono.fromSupplier(() -> dslContext.insertInto(TABLE)
                .columns(FIELD_NAME, FIELD_TYPE)
                .values(name, valueFromType(type))
                .returning(FIELD_ID)
                .fetchOne()
                .get(FIELD_ID))
            .subscribeOn(boundedElastic());
    }

    public Mono<List<LocationDeclaration>> getLocations() {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
                .fetch(r -> r.map(LocationRepository::locationDeclarationFromRecord)))
            .subscribeOn(boundedElastic());
    }

    private static LocationDeclaration locationDeclarationFromRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var name = record.get(FIELD_NAME);
        final var typeName = record.get(FIELD_TYPE);

        final var type = switch (typeName) {
            case "dump" -> DUMP;
            case "face" -> FACE;
            case "hole" -> HOLE;
            case "stockpile" -> STOCKPILE;
            default -> throw new IllegalArgumentException("Unsupported location type: " + typeName);
        };

        return new LocationDeclaration(id, name, type);
    }

    private static String valueFromType(LocationType type) {
        if (!TYPE_TO_VALUE.containsKey(type)) {
            throw new InternalServerError("Unsupported location type: " + type);
        }
        return TYPE_TO_VALUE.get(type);
    }
}
