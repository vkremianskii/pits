package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.app.model.Location;
import com.github.vkremianskii.pits.registry.app.model.LocationType;
import com.github.vkremianskii.pits.registry.app.model.location.Face;
import com.github.vkremianskii.pits.registry.app.model.location.Hole;
import com.github.vkremianskii.pits.registry.app.model.location.Dump;
import com.github.vkremianskii.pits.registry.app.model.location.Stockpile;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.vkremianskii.pits.registry.app.model.LocationType.*;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class LocationRepository {
    private static final Table<?> TABLE = table("location");
    private static final Field<Integer> FIELD_ID = field("id", Integer.class);
    private static final Field<String> FIELD_NAME = field("name", String.class);
    private static final Field<String> FIELD_TYPE = field("type", String.class);

    private final DSLContext dslContext;

    public LocationRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromCompletionStage(dslContext.deleteFrom(TABLE).executeAsync()).then();
    }

    public Mono<List<Location>> getLocations() {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE).fetchAsync()
                .thenApply(r -> r.map(LocationRepository::locationFromRecord)));
    }

    public Mono<Void> put(String name, LocationType type) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_NAME, FIELD_TYPE)
                .values(name, type.name().toLowerCase())
                .executeAsync()).then();
    }

    private static Location locationFromRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var name = record.get(FIELD_NAME);
        final var typeName = record.get(FIELD_TYPE);

        final var type = LocationType.valueOf(typeName.toUpperCase());
        return switch (type) {
            case DUMP -> new Dump(id, name);
            case HOLE -> new Hole(id, name);
            case FACE -> new Face(id, name);
            case STOCKPILE -> new Stockpile(id, name);
        };
    }
}
