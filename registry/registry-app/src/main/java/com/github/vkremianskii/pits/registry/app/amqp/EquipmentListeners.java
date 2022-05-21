package com.github.vkremianskii.pits.registry.app.amqp;

import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.TruckPayloadWeightChanged;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.registry.app.amqp.AmqpConfig.QUEUE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.registry.app.amqp.AmqpConfig.QUEUE_TRUCK_PAYLOAD_WEIGHT;
import static java.util.Objects.requireNonNull;

@Component
public class EquipmentListeners {
    private final EquipmentRepository equipmentRepository;

    public EquipmentListeners(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_POSITION)
    void handleEquipmentPosition(EquipmentPositionChanged message) {
        equipmentRepository.updateEquipmentPosition(
                message.getEquipmentId(),
                new Position(
                        message.getLatitude(),
                        message.getLongitude(),
                        message.getElevation()));
    }

    @RabbitListener(queues = QUEUE_TRUCK_PAYLOAD_WEIGHT)
    void handleTruckPayloadWeight(TruckPayloadWeightChanged message) {
        equipmentRepository.updateTruckPayloadWeight(
                message.getEquipmentId(),
                message.getWeight());
    }
}
