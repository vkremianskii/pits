package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.app.error.InternalServerError;
import com.github.vkremianskii.pits.registry.types.model.Location;
import com.github.vkremianskii.pits.registry.types.model.LocationType;
import com.github.vkremianskii.pits.registry.types.model.location.Dump;
import com.github.vkremianskii.pits.registry.types.model.location.Face;
import com.github.vkremianskii.pits.registry.types.model.location.Hole;
import com.github.vkremianskii.pits.registry.types.model.location.Stockpile;
import org.jooq.*;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.*;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.*;

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
        return Mono.fromCompletionStage(dslContext.deleteFrom(TABLE).executeAsync()).then();
    }

    public Mono<Void> insert(String name, LocationType type) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_NAME, FIELD_TYPE)
                .values(name, valueFromType(type))
                .executeAsync()).then();
    }

    public Mono<List<Location>> getLocations() {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE).fetchAsync()
                .thenApply(r -> r.map(LocationRepository::locationFromRecord)));
    }

    private static Location locationFromRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var name = record.get(FIELD_NAME);
        final var type = record.get(FIELD_TYPE);

        return switch (type) {
            case "dump" -> new Dump(id, name);
            case "hole" -> new Hole(id, name);
            case "face" -> new Face(id, name);
            case "stockpile" -> new Stockpile(id, name);
            default -> throw new InternalServerError("Invalid location type: " + type);
        };
    }

    private static String valueFromType(LocationType type) {
        if (!TYPE_TO_VALUE.containsKey(type)) {
            throw new InternalServerError("Unsupported location type: " + type);
        }
        return TYPE_TO_VALUE.get(type);
    }
}
