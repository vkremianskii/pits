package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.core.types.model.LocationId;
import com.github.vkremianskii.pits.core.types.model.LocationType;
import com.github.vkremianskii.pits.core.web.error.InternalServerError;
import com.github.vkremianskii.pits.registry.types.model.LocationDeclaration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.LocationId.locationId;
import static com.github.vkremianskii.pits.core.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.core.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.core.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.core.types.model.LocationType.STOCKPILE;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class LocationRepository {

    private static final Table<?> TABLE = table("location");
    private static final Field<UUID> FIELD_ID = field("id", UUID.class);
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
        return Mono.fromRunnable(() -> dslContext.deleteFrom(TABLE).execute());
    }

    public Mono<List<LocationDeclaration>> getLocations() {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
            .fetch(r -> r.map(LocationRepository::locationDeclarationFromRecord)));
    }

    public Mono<Void> createLocation(LocationId id, String name, LocationType type) {
        return Mono.fromRunnable(() -> dslContext.insertInto(TABLE)
            .columns(FIELD_ID, FIELD_NAME, FIELD_TYPE)
            .values(id.value, name, valueFromType(type))
            .execute());
    }

    private static LocationDeclaration locationDeclarationFromRecord(org.jooq.Record record) {
        final var id = locationId(record.get(FIELD_ID));
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
