package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.*;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.*;

@Repository
public class EquipmentRepository {
    private static final Table<?> TABLE = table("equipment");
    private static final Field<Integer> FIELD_ID = field("id", Integer.class);
    private static final Field<String> FIELD_NAME = field("name", String.class);
    private static final Field<EquipmentTypeEnum> FIELD_TYPE = field("type", EquipmentTypeEnum.class);
    private static final Field<EquipmentStateEnum> FIELD_STATE = field("state", EquipmentStateEnum.class);
    private static final Field<BigDecimal> FIELD_LATITUDE = field("latitude", BigDecimal.class);
    private static final Field<BigDecimal> FIELD_LONGITUDE = field("longitude", BigDecimal.class);
    private static final Field<Short> FIELD_ELEVATION = field("elevation", Short.class);
    private static final Field<Integer> FIELD_PAYLOAD = field("payload", Integer.class);
    private static final Field<Short> FIELD_LOAD_RADIUS = field("load_radius", Short.class);

    private static final Map<EquipmentType, EquipmentTypeEnum> TYPE_TO_TYPE_ENUM = Map.of(
            DOZER, EquipmentTypeEnum.DOZER,
            DRILL, EquipmentTypeEnum.DRILL,
            SHOVEL, EquipmentTypeEnum.SHOVEL,
            TRUCK, EquipmentTypeEnum.TRUCK);

    private static final Map<EquipmentState, EquipmentStateEnum> STATE_TO_STATE_ENUM = Map.of(
            TruckState.EMPTY, EquipmentStateEnum.TRUCK_EMPTY,
            TruckState.WAIT_LOAD, EquipmentStateEnum.TRUCK_WAIT_LOAD,
            TruckState.LOAD, EquipmentStateEnum.TRUCK_LOAD,
            TruckState.HAUL, EquipmentStateEnum.TRUCK_HAUL,
            TruckState.UNLOAD, EquipmentStateEnum.TRUCK_UNLOAD);

    private static final Map<EquipmentStateEnum, EquipmentState> STATE_ENUM_TO_STATE = STATE_TO_STATE_ENUM.entrySet().stream()
            .collect(toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final DSLContext dslContext;

    public EquipmentRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromCompletionStage(dslContext.deleteFrom(TABLE).executeAsync()).then();
    }

    public Mono<Void> insert(String name, EquipmentType type, Short loadRadius) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_NAME, FIELD_TYPE, FIELD_LOAD_RADIUS)
                .values(name, typeEnumFromType(type), loadRadius)
                .executeAsync()).then();
    }

    public Mono<Void> insert(int equipmentId, String name, EquipmentType type, Short loadRadius) {
        return Mono.fromCompletionStage(dslContext.insertInto(TABLE)
                .columns(FIELD_ID, FIELD_NAME, FIELD_TYPE, FIELD_LOAD_RADIUS)
                .values(equipmentId, name, typeEnumFromType(type), loadRadius)
                .executeAsync()).then();
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

    public Mono<Void> updateEquipmentState(int equipmentId, EquipmentState state) {
        return Mono.fromCompletionStage(dslContext.update(TABLE)
                .set(FIELD_STATE, stateEnumFromState(state))
                .where(FIELD_ID.eq(equipmentId))
                .executeAsync()).then();
    }

    public Mono<Void> updateEquipmentPosition(int equipmentId, Position position) {
        return Mono.fromCompletionStage(dslContext.update(TABLE)
                .set(FIELD_LATITUDE, BigDecimal.valueOf(position.getLatitude()))
                .set(FIELD_LONGITUDE, BigDecimal.valueOf(position.getLongitude()))
                .set(FIELD_ELEVATION, (short) position.getElevation())
                .where(FIELD_ID.eq(equipmentId))
                .executeAsync()).then();
    }

    public Mono<Void> updateEquipmentPayload(int equipmentId, int payload) {
        return Mono.fromCompletionStage(dslContext.update(TABLE)
                .set(FIELD_PAYLOAD, payload)
                .where(FIELD_ID.eq(equipmentId))
                .executeAsync()).then();
    }

    private static Equipment equipmentFromRecord(org.jooq.Record record) {
        final var id = record.get(FIELD_ID);
        final var name = record.get(FIELD_NAME);
        final var type = record.get(FIELD_TYPE);
        final var state = record.get(FIELD_STATE);
        final var latitude = record.get(FIELD_LATITUDE);
        final var longitude = record.get(FIELD_LONGITUDE);
        final var elevation = record.get(FIELD_ELEVATION);
        final var payload = record.get(FIELD_PAYLOAD);
        final var loadRadius = Optional.ofNullable(record.get(FIELD_LOAD_RADIUS));

        Position position = null;
        if (latitude != null && longitude != null && elevation != null) {
            position = new Position(latitude.doubleValue(), longitude.doubleValue(), elevation);
        }

        return switch (type) {
            case DOZER -> new Dozer(
                    id,
                    name,
                    stateFromStateEnum(state, DozerState.class),
                    position);
            case DRILL -> new Drill(
                    id,
                    name,
                    stateFromStateEnum(state, DrillState.class),
                    position);
            case SHOVEL -> new Shovel(
                    id,
                    name,
                    loadRadius.map(Short::intValue).orElse(0),
                    stateFromStateEnum(state, ShovelState.class),
                    position);
            case TRUCK -> new Truck(
                    id,
                    name,
                    stateFromStateEnum(state, TruckState.class),
                    position,
                    payload);
        };
    }

    private static EquipmentTypeEnum typeEnumFromType(EquipmentType type) {
        return TYPE_TO_TYPE_ENUM.get(type);
    }

    private static EquipmentStateEnum stateEnumFromState(EquipmentState state) {
        return STATE_TO_STATE_ENUM.get(state);
    }

    private static <T> T stateFromStateEnum(EquipmentStateEnum value, Class<T> cls) {
        if (value == null) {
            return null;
        }
        final var state = STATE_ENUM_TO_STATE.get(value);
        if (state == null) {
            return null;
        }
        return cls.cast(state);
    }

    private enum EquipmentTypeEnum implements EnumType {
        DOZER,
        DRILL,
        SHOVEL,
        TRUCK;

        @Override
        public @NotNull String getLiteral() {
            return name().toLowerCase();
        }

        @Override
        public @Nullable Schema getSchema() {
            return schema("public");
        }

        @Override
        public @Nullable String getName() {
            return "equipment_type";
        }
    }

    private enum EquipmentStateEnum implements EnumType {
        TRUCK_EMPTY,
        TRUCK_WAIT_LOAD,
        TRUCK_LOAD,
        TRUCK_HAUL,
        TRUCK_UNLOAD;

        @Override
        public @NotNull String getLiteral() {
            return name().toLowerCase();
        }

        @Override
        public @Nullable Schema getSchema() {
            return schema("public");
        }

        @Override
        public @Nullable String getName() {
            return "equipment_state";
        }
    }
}
