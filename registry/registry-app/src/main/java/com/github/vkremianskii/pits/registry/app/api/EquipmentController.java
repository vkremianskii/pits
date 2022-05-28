package com.github.vkremianskii.pits.registry.app.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.vkremianskii.pits.core.web.error.BadRequestError;
import com.github.vkremianskii.pits.core.web.error.NotFoundError;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.types.ApiVersion;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentRequest;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentResponse;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.equipment.DozerState;
import com.github.vkremianskii.pits.registry.types.model.equipment.DrillState;
import com.github.vkremianskii.pits.registry.types.model.equipment.ShovelState;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.github.vkremianskii.pits.registry.types.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.types.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static java.util.Objects.requireNonNull;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentRepository equipmentRepository;
    private final ObjectMapper objectMapper;
    private final PlatformTransactionManager transactionManager;

    public EquipmentController(EquipmentRepository equipmentRepository,
                               ObjectMapper objectMapper,
                               PlatformTransactionManager transactionManager) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
        this.objectMapper = requireNonNull(objectMapper);
        this.transactionManager = requireNonNull(transactionManager);
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
        final var equipmentId = UUID.randomUUID();
        final var txDefiniton = new DefaultTransactionDefinition(PROPAGATION_REQUIRES_NEW);
        final var txStatus = transactionManager.getTransaction(txDefiniton);

        return equipmentRepository.createEquipment(equipmentId, request.name(), request.type(), null)
            .thenReturn(new CreateEquipmentResponse(equipmentId))
            .doOnSuccess(__ -> transactionManager.commit(txStatus))
            .doOnError(__ -> transactionManager.rollback(txStatus));
    }

    @PostMapping("/{id}/state")
    public Mono<Void> updateEquipmentState(@PathVariable("id") UUID equipmentId,
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
