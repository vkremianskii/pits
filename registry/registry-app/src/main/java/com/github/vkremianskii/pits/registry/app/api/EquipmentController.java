package com.github.vkremianskii.pits.registry.app.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.core.web.error.BadRequestError;
import com.github.vkremianskii.pits.core.web.error.NotFoundError;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.equipment.DozerState;
import com.github.vkremianskii.pits.registry.types.model.equipment.DrillState;
import com.github.vkremianskii.pits.registry.types.model.equipment.ShovelState;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public Mono<List<Equipment>> getEquipment() {
        return equipmentRepository.getEquipment();
    }

    @PostMapping("/{id}/state")
    public Mono<Void> updateEquipmentState(@PathVariable("id") int equipmentId,
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
