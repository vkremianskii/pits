package com.github.vkremianskii.pits.registry.data;

import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.EquipmentState;
import com.github.vkremianskii.pits.registry.model.EquipmentType;
import com.github.vkremianskii.pits.registry.model.Position;
import com.github.vkremianskii.pits.registry.model.equipment.*;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class EquipmentRepository {
    private static final Table<?> TABLE = table("equipment");
    private static final Field<Integer> FIELD_ID = field("id", Integer.class);
    private static final Field<String> FIELD_NAME = field("name", String.class);
    private static final Field<String> FIELD_STATE = field("state", String.class);
    private static final Field<String> FIELD_TYPE = field("type", String.class);
    private static final Field<BigDecimal> FIELD_LATITUDE = field("latitude", BigDecimal.class);
    private static final Field<BigDecimal> FIELD_LONGITUDE = field("longitude", BigDecimal.class);
    private static final Field<Short> FIELD_ELEVATION = field("elevation", Short.class);

    private static final Map<EquipmentState, String> STATE_TO_FULL_NAME = Map.of(
            TruckState.EMPTY, "truck_empty",
            TruckState.LOAD, "truck_load",
            TruckState.HAUL, "truck_haul",
            TruckState.UNLOAD, "truck_unload");

    private static final Map<String, EquipmentState> FULL_NAME_TO_STATE = STATE_TO_FULL_NAME.entrySet().stream()
            .collect(toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final DSLContext dslContext;

    public EquipmentRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromCompletionStage(dslContext.deleteFrom(TABLE).executeAsync()).then();
    }

    public Mono<List<Equipment>> getEquipment() {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE).fetchAsync()
                .thenApply(r -> r.map(EquipmentRepository::equipmentFromRecord)));
    }

    public Mono<Optional<Equipment>> getEquipmentById(int equipmentId) {
        return Mono.fromCompletionStage(dslContext.selectFrom(TABLE)
                .where(FIELD_ID.eq(equipmentId))
                .fetchAsync()
                .thenApply(r -> r.map(EquipmentRepository::equipmentFromRecord))
                .thenApply(e -> !e.isEmpty() ? e.get(0) : null)
                .thenApply(Optional::ofNullable));
    }

    public Mono<Void> put(String name, EquipmentType type) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_NAME, FIELD_TYPE)
                .values(name, type.name())
                .executeAsync()).then();
    }

    public Mono<Void> put(int equipmentId, String name, EquipmentType type) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_ID, FIELD_NAME, FIELD_TYPE)
                .values(equipmentId, name, type.name())
                .executeAsync()).then();
    }

    public Mono<Void> updateEquipmentState(int equipmentId, EquipmentState state) {
        return Mono.fromCompletionStage(dslContext.update(TABLE)
                .set(FIELD_STATE, stateToFullName(state))
                .where(FIELD_ID.eq(equipmentId))
                .executeAsync()).then();
    }

    public Mono<Void> updateEquipmentPosition(int equipmentId, Position position) {
        return Mono.fromCompletionStage(dslContext.update(TABLE)
                .set(FIELD_LATITUDE, BigDecimal.valueOf(position.latitude()))
                .set(FIELD_LONGITUDE, BigDecimal.valueOf(position.longitude()))
                .set(FIELD_ELEVATION, (short) position.elevation())
                .where(FIELD_ID.eq(equipmentId))
                .executeAsync()).then();
    }

    private static Equipment equipmentFromRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var name = record.get(FIELD_NAME);
        final var typeName = record.get(FIELD_TYPE);
        final var stateName = record.get(FIELD_STATE);
        final var latitude = record.get(FIELD_LATITUDE);
        final var longitude = record.get(FIELD_LONGITUDE);
        final var elevation = record.get(FIELD_ELEVATION);

        Position position = null;
        if (latitude != null && longitude != null && elevation != null) {
            position = new Position(latitude.doubleValue(), longitude.doubleValue(), elevation);
        }

        final var type = EquipmentType.valueOf(typeName.toUpperCase());
        return switch (type) {
            case DOZER -> new Dozer(
                    id,
                    name,
                    fullNameToState(stateName, DozerState.class),
                    position);
            case DRILL -> new Drill(
                    id,
                    name,
                    fullNameToState(stateName, DrillState.class),
                    position);
            case SHOVEL -> new Shovel(
                    id,
                    name,
                    fullNameToState(stateName, ShovelState.class),
                    position);
            case TRUCK -> new Truck(
                    id,
                    name,
                    fullNameToState(stateName, TruckState.class),
                    position);
        };
    }

    private static String stateToFullName(EquipmentState state) {
        return STATE_TO_FULL_NAME.get(state);
    }

    private static <T> T fullNameToState(String name, Class<T> cls) {
        final var state = FULL_NAME_TO_STATE.get(name);
        if (state == null) {
            return null;
        }
        return cls.cast(state);
    }
}
