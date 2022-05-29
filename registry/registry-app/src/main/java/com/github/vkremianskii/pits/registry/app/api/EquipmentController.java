package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.EquipmentState;
import com.github.vkremianskii.pits.core.types.model.EquipmentType;
import com.github.vkremianskii.pits.core.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.core.types.model.equipment.Drill;
import com.github.vkremianskii.pits.core.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.core.types.model.equipment.Truck;
import com.github.vkremianskii.pits.core.web.error.BadRequestError;
import com.github.vkremianskii.pits.core.web.error.NotFoundError;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.types.ApiVersion;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentRequest;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentResponse;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.types.dto.UpdateEquipmentStateRequest;
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

    public EquipmentController(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
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
                                           @RequestBody UpdateEquipmentStateRequest request) {
        return equipmentRepository.getEquipmentById(equipmentId)
            .flatMap(equipment -> equipment
                .map(e -> {
                    throwIfInvalidState(e.type, request.state());
                    return equipmentRepository.updateEquipmentState(equipmentId, request.state());
                })
                .orElse(Mono.error(new NotFoundError())));
    }

    private static void throwIfInvalidState(EquipmentType type, EquipmentState state) {
        final var valid = switch (type) {
            case DOZER -> Dozer.isValidState(state);
            case DRILL -> Drill.isValidState(state);
            case SHOVEL -> Shovel.isValidState(state);
            case TRUCK -> Truck.isValidState(state);
        };
        if (!valid) {
            throw new BadRequestError("Invalid state: " + state);
        }
    }
}
