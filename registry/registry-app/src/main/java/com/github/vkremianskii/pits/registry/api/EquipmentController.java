package com.github.vkremianskii.pits.registry.api;

import com.github.vkremianskii.pits.core.web.error.BadRequestError;
import com.github.vkremianskii.pits.core.web.error.NotFoundError;
import com.github.vkremianskii.pits.registry.ApiVersion;
import com.github.vkremianskii.pits.registry.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.dto.CreateEquipmentRequest;
import com.github.vkremianskii.pits.registry.dto.CreateEquipmentResponse;
import com.github.vkremianskii.pits.registry.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.dto.UpdateEquipmentStateRequest;
import com.github.vkremianskii.pits.registry.model.EquipmentId;
import com.github.vkremianskii.pits.registry.model.EquipmentState;
import com.github.vkremianskii.pits.registry.model.EquipmentType;
import com.github.vkremianskii.pits.registry.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.model.equipment.Truck;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.registry.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static com.github.vkremianskii.pits.registry.model.EquipmentId.equipmentId;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;

    public EquipmentController(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('registry:equipment:list', 'admin')")
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
    @PreAuthorize("hasAnyAuthority('registry:equipment:create', 'admin')")
    public Mono<CreateEquipmentResponse> createEquipment(@RequestBody CreateEquipmentRequest request) {
        final var equipmentId = equipmentId(randomUUID());
        return equipmentRepository.createEquipment(equipmentId, request.name(), request.type(), null)
            .thenReturn(new CreateEquipmentResponse(equipmentId));
    }

    @PostMapping("/{id}/state")
    @PreAuthorize("hasAnyAuthority('registry:equipment:update', 'admin')")
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
