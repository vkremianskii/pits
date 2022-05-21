package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.app.model.Position;
import com.github.vkremianskii.pits.registry.app.model.Equipment;
import com.github.vkremianskii.pits.registry.types.UpdateEquipmentPositionRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    private final EquipmentRepository equipmentRepository;

    public EquipmentController(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
    }

    @GetMapping
    public Mono<List<Equipment>> getEquipment() {
        return equipmentRepository.getEquipment();
    }

    @PostMapping("/{id}/position")
    public Mono<Void> updateEquipmentPosition(@PathVariable("id") int equipmentId,
                                              @RequestBody UpdateEquipmentPositionRequest request) {
        return equipmentRepository.updateEquipmentPosition(
                equipmentId,
                new Position(request.getLatitude(), request.getLongitude(), request.getElevation()));
    }

    @PostMapping("/{id}/payload/weight")
    public Mono<Void> updateTruckPayloadWeight(@PathVariable("id") int equipmentId,
                                               @RequestBody UpdateEquipmentPositionRequest request) {
        return equipmentRepository.updateEquipmentPosition(
                equipmentId,
                new Position(request.getLatitude(), request.getLongitude(), request.getElevation()));
    }
}
