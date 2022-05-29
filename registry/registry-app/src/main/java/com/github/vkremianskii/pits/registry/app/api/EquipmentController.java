package com.github.vkremianskii.pits.registry.app.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.vkremianskii.pits.core.types.model.Equipment;
import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.EquipmentState;
import com.github.vkremianskii.pits.core.types.model.EquipmentType;
import com.github.vkremianskii.pits.core.types.model.equipment.DozerState;
import com.github.vkremianskii.pits.core.types.model.equipment.DrillState;
import com.github.vkremianskii.pits.core.types.model.equipment.ShovelState;
import com.github.vkremianskii.pits.core.types.model.equipment.TruckState;
import com.github.vkremianskii.pits.core.web.error.BadRequestError;
import com.github.vkremianskii.pits.core.web.error.NotFoundError;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.types.ApiVersion;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentRequest;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentResponse;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentId.equipmentId;
import static com.github.vkremianskii.pits.registry.types.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.types.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;
    private final ObjectMapper objectMapper;

    public EquipmentController(EquipmentRepository equipmentRepository,
                               ObjectMapper objectMapper) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
        this.objectMapper = requireNonNull(objectMapper);
    }

    @GetMapping
    public Mono<ResponseEntity<?>> getEquipment(@RequestHeader(name = API_VERSION, required = false) ApiVersion version) {
        if (version != null && version.isGreaterThanOrEqual(EQUIPMENT_RESPONSE_OBJECT)) {
            return equipmentRepository.getEquipment()
                .map(EquipmentResponse::new)
                .map(ResponseEntity::ok);
        } else {
            return equipmentRepository.getEquipment()
                .map(ResponseEntity::ok);
        }
    }

    @PostMapping
    public Mono<CreateEquipmentResponse> createEquipment(@RequestBody CreateEquipmentRequest request) {
        final var equipmentId = equipmentId(UUID.randomUUID());
        return equipmentRepository.createEquipment(equipmentId, request.name(), request.type(), null)
            .thenReturn(new CreateEquipmentResponse(equipmentId));
    }

    @PostMapping("/{id}/state")
    public Mono<Void> updateEquipmentState(@PathVariable("id") EquipmentId equipmentId,
                                           @RequestBody FuzzyUpdateEquipmentStateRequest request) {
        return equipmentRepository.getEquipmentById(equipmentId)
            .flatMap(equipment -> equipment
                .map(e -> updateEquipmentState(e, request.state))
                .orElse(Mono.error(new NotFoundError())));
    }

    private Mono<Void> updateEquipmentState(Equipment equipment, TextNode state) {
        return equipmentRepository.updateEquipmentState(
            equipment.id,
            deserializeEquipmentState(state, equipment.type));
    }

    private EquipmentState deserializeEquipmentState(TextNode state, EquipmentType type) {
        final var clazz = switch (type) {
            case DOZER -> DozerState.class;
            case DRILL -> DrillState.class;
            case SHOVEL -> ShovelState.class;
            case TRUCK -> TruckState.class;
        };
        try {
            return objectMapper.treeToValue(state, clazz);
        } catch (JsonProcessingException e) {
            throw new BadRequestError(e);
        }
    }

    public static class FuzzyUpdateEquipmentStateRequest {

        public TextNode state;
    }
}
