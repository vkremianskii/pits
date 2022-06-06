package com.github.vkremianskii.pits.registry.data;

import com.github.vkremianskii.pits.core.data.TransactionalJooq;
import com.github.vkremianskii.pits.core.web.error.InternalServerError;
import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.EquipmentId;
import com.github.vkremianskii.pits.registry.model.EquipmentState;
import com.github.vkremianskii.pits.registry.model.EquipmentType;
import com.github.vkremianskii.pits.registry.model.Position;
import com.github.vkremianskii.pits.registry.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.model.equipment.Truck;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.vkremianskii.pits.core.util.NullableUtils.mapNotNull;
import static com.github.vkremianskii.pits.registry.model.EquipmentId.equipmentId;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.TRUCK;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class EquipmentRepository {

    private static final Table<?> TABLE = table("equipment");
    private static final Field<UUID> FIELD_ID = field("id", UUID.class);
    private static final Field<String> FIELD_NAME = field("name", String.class);
    private static final Field<String> FIELD_TYPE = field("type", String.class);
    private static final Field<String> FIELD_STATE = field("state", String.class);
    private static final Field<Double> FIELD_LATITUDE = field("latitude", Double.class);
    private static final Field<Double> FIELD_LONGITUDE = field("longitude", Double.class);
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
        Truck.STATE_EMPTY, "truck_empty",
        Truck.STATE_WAIT_LOAD, "truck_wait_load",
        Truck.STATE_LOAD, "truck_load",
        Truck.STATE_HAUL, "truck_haul",
        Truck.STATE_UNLOAD, "truck_unload");

    private static final Map<String, EquipmentState> VALUE_TO_STATE = STATE_TO_VALUE.entrySet().stream()
        .collect(toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final TransactionalJooq transactionalJooq;

    public EquipmentRepository(TransactionalJooq transactionalJooq) {
        this.transactionalJooq = requireNonNull(transactionalJooq);
    }

    public Mono<Void> clear() {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.deleteFrom(TABLE)))
            .then();
    }

    public Mono<List<Equipment>> getEquipment() {
        return transactionalJooq.inTransactionalContext(ctx -> Flux.from(ctx.selectFrom(TABLE))
            .map(r -> r.map(EquipmentRepository::equipmentFromRecord))
            .collectList());
    }

    public Mono<Optional<Equipment>> getEquipmentById(EquipmentId equipmentId) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.selectFrom(TABLE)
                .where(FIELD_ID.eq(equipmentId.value)))
            .map(r -> r.map(EquipmentRepository::equipmentFromRecord))
            .map(Optional::of)
            .switchIfEmpty(Mono.just(Optional.empty())));
    }

    public Mono<Void> createEquipment(EquipmentId id, String name, EquipmentType type, Short loadRadius) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.insertInto(TABLE)
                .columns(FIELD_ID, FIELD_NAME, FIELD_TYPE, FIELD_LOAD_RADIUS)
                .values(id.value, name, valueFromType(type), loadRadius)))
            .then();
    }

    public Mono<Void> updateEquipmentState(EquipmentId equipmentId, EquipmentState state) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.update(TABLE)
                .set(FIELD_STATE, valueFromState(state))
                .where(FIELD_ID.eq(equipmentId.value))))
            .then();
    }

    public Mono<Void> updateEquipmentPosition(EquipmentId equipmentId, Position position) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.update(TABLE)
                .set(FIELD_LATITUDE, position.latitude())
                .set(FIELD_LONGITUDE, position.longitude())
                .set(FIELD_ELEVATION, (short) position.elevation())
                .where(FIELD_ID.eq(equipmentId.value))))
            .then();
    }

    public Mono<Void> updateEquipmentPayload(EquipmentId equipmentId, int payload) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.update(TABLE)
                .set(FIELD_PAYLOAD, payload)
                .where(FIELD_ID.eq(equipmentId.value))))
            .then();
    }

    private static Equipment equipmentFromRecord(org.jooq.Record record) {
        final var id = equipmentId(record.get(FIELD_ID));
        final var name = record.get(FIELD_NAME);
        final var type = record.get(FIELD_TYPE);
        final var state = record.get(FIELD_STATE);
        final var latitude = record.get(FIELD_LATITUDE);
        final var longitude = record.get(FIELD_LONGITUDE);
        final var elevation = record.get(FIELD_ELEVATION);
        final var payload = record.get(FIELD_PAYLOAD);
        final var loadRadius = record.get(FIELD_LOAD_RADIUS);

        Position position = null;
        if (latitude != null && longitude != null && elevation != null) {
            position = new Position(latitude, longitude, elevation);
        }

        return switch (type) {
            case "dozer" -> new Dozer(id, name, stateFromValue(state), position);
            case "drill" -> new Drill(id, name, stateFromValue(state), position);
            case "shovel" -> new Shovel(
                id,
                name,
                mapNotNull(loadRadius, DEFAULT_SHOVEL_LOAD_RADIUS, Short::intValue),
                stateFromValue(state),
                position);
            case "truck" -> new Truck(id, name, stateFromValue(state), position, payload);
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

    private static EquipmentState stateFromValue(String value) {
        if (value == null) {
            return null;
        }
        if (!VALUE_TO_STATE.containsKey(value)) {
            throw new InternalServerError("Invalid equipment state: " + value);
        }
        return VALUE_TO_STATE.get(value);
    }
}
