package com.github.vkremianskii.pits.registry.amqp;

import com.github.vkremianskii.pits.registry.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.registry.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.model.Position;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.registry.amqp.AmqpConfig.QUEUE_EQUIPMENT_PAYLOAD;
import static com.github.vkremianskii.pits.registry.amqp.AmqpConfig.QUEUE_EQUIPMENT_POSITION;
import static java.util.Objects.requireNonNull;

@Component
public class EquipmentListeners {

    private final EquipmentRepository equipmentRepository;

    public EquipmentListeners(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_POSITION)
    void handlePositionChanged(EquipmentPositionChanged message) {
        equipmentRepository.updateEquipmentPosition(
                message.equipmentId(),
                new Position(
                    message.latitude(),
                    message.longitude(),
                    message.elevation()))
            .block();
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_PAYLOAD)
    void handlePayloadChanged(EquipmentPayloadChanged message) {
        equipmentRepository.updateEquipmentPayload(
                message.equipmentId(),
                message.payload())
            .block();
    }
}
