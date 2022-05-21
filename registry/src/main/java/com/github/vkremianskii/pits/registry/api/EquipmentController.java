package com.github.vkremianskii.pits.registry.api;

import com.github.vkremianskii.pits.registry.api.dto.UpdateEquipmentPositionRequest;
import com.github.vkremianskii.pits.registry.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.Position;
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
}
