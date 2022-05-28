package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.core.web.error.InternalServerError;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.types.model.equipment.DozerState;
import com.github.vkremianskii.pits.registry.types.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.types.model.equipment.DrillState;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.ShovelState;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.table;

@Repository
public class EquipmentRepository {

    private static final Table<?> TABLE = table("equipment");
    private static final Field<UUID> FIELD_ID = field("id", UUID.class);
    private static final Field<String> FIELD_NAME = field("name", String.class);
    private static final Field<String> FIELD_TYPE = field("type", String.class);
    private static final Field<String> FIELD_STATE = field("state", String.class);
    private static final Field<BigDecimal> FIELD_LATITUDE = field("latitude", BigDecimal.class);
    private static final Field<BigDecimal> FIELD_LONGITUDE = field("longitude", BigDecimal.class);
    private static final Field<Short> FIELD_ELEVATION = field("elevation", Short.class);
    private static final Field<Integer> FIELD_PAYLOAD = field("payload", Integer.class);
    private static final Field<Short> FIELD_LOAD_RADIUS = field("load_radius", Short.class);

    private static final int DEFAULT_SHOVEL_LOAD_RADIUS = 20;

    private static final Map<EquipmentType, String> TYPE_TO_VALUE = Map.of(
        DOZER, "dozer",
        DRILL, "drill",
        SHOVEL, "shovel",
        TRUCK, "truck");

    private static final Map<EquipmentState, String> STATE_TO_VALUE = Map.of(
        TruckState.EMPTY, "truck_empty",
        TruckState.WAIT_LOAD, "truck_wait_load",
        TruckState.LOAD, "truck_load",
        TruckState.HAUL, "truck_haul",
        TruckState.UNLOAD, "truck_unload");

    private static final Map<String, EquipmentState> VALUE_TO_STATE = STATE_TO_VALUE.entrySet().stream()
        .collect(toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final DSLContext dslContext;

    public EquipmentRepository(DSLContext dslContext) {
        this.dslContext = requireNonNull(dslContext);
    }

    public Mono<Void> clear() {
        return Mono.fromRunnable(() -> dslContext.deleteFrom(TABLE).execute());
    }

    public Mono<List<Equipment>> getEquipment() {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
            .fetch(r -> r.map(EquipmentRepository::equipmentFromRecord)));
    }

    public Mono<Optional<Equipment>> getEquipmentById(UUID equipmentId) {
        return Mono.fromSupplier(() -> dslContext.selectFrom(TABLE)
            .where(FIELD_ID.eq(equipmentId))
            .fetchOptional(r -> r.map(EquipmentRepository::equipmentFromRecord)));
    }

    public Mono<Void> createEquipment(UUID id, String name, EquipmentType type, Short loadRadius) {
        return Mono.fromRunnable(() -> dslContext.insertInto(TABLE)
            .columns(FIELD_ID, FIELD_NAME, FIELD_TYPE, FIELD_LOAD_RADIUS)
            .values(id, name, valueFromType(type), loadRadius)
            .execute());
    }

    public Mono<Void> updateEquipmentState(UUID equipmentId, EquipmentState state) {
        return Mono.fromRunnable(() -> dslContext.update(TABLE)
            .set(FIELD_STATE, valueFromState(state))
            .where(FIELD_ID.eq(equipmentId))
            .execute());
    }

    public Mono<Void> updateEquipmentPosition(UUID equipmentId, Position position) {
        return Mono.fromRunnable(() -> dslContext.update(TABLE)
            .set(FIELD_LATITUDE, BigDecimal.valueOf(position.latitude()))
            .set(FIELD_LONGITUDE, BigDecimal.valueOf(position.longitude()))
            .set(FIELD_ELEVATION, (short) position.elevation())
            .where(FIELD_ID.eq(equipmentId))
            .execute());
    }

    public Mono<Void> updateEquipmentPayload(UUID equipmentId, int payload) {
        return Mono.fromRunnable(() -> dslContext.update(TABLE)
            .set(FIELD_PAYLOAD, payload)
            .where(FIELD_ID.eq(equipmentId))
            .execute());
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
            case "dozer" -> new Dozer(
                id,
                name,
                stateFromValue(state, DozerState.class),
                position);
            case "drill" -> new Drill(
                id,
                name,
                stateFromValue(state, DrillState.class),
                position);
            case "shovel" -> new Shovel(
                id,
                name,
                loadRadius.map(Short::intValue).orElse(DEFAULT_SHOVEL_LOAD_RADIUS),
                stateFromValue(state, ShovelState.class),
                position);
            case "truck" -> new Truck(
                id,
                name,
                stateFromValue(state, TruckState.class),
                position,
                payload);
            default -> throw new InternalServerError("Invalid equipment type: " + type);
        };
    }

    private static String valueFromType(EquipmentType type) {
        if (!TYPE_TO_VALUE.containsKey(type)) {
            throw new InternalServerError("Unsupported equipment type: " + type);
        }
        return TYPE_TO_VALUE.get(type);
    }

    private static String valueFromState(EquipmentState state) {
        if (!STATE_TO_VALUE.containsKey(state)) {
            throw new InternalServerError("Unsupported equipment state: " + state);
        }
        return STATE_TO_VALUE.get(state);
    }

    private static <T> T stateFromValue(String value, Class<T> cls) {
        if (value == null) {
            return null;
        }
        if (!VALUE_TO_STATE.containsKey(value)) {
            throw new InternalServerError("Invalid equipment state: " + value);
        }
        return cls.cast(VALUE_TO_STATE.get(value));
    }
}
