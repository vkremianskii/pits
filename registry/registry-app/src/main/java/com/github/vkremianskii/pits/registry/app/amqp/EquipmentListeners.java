package com.github.vkremianskii.pits.registry.app.amqp;

import com.github.vkremianskii.pits.core.types.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.core.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.core.types.model.Position;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.registry.app.amqp.AmqpConfig.QUEUE_EQUIPMENT_PAYLOAD;
import static com.github.vkremianskii.pits.registry.app.amqp.AmqpConfig.QUEUE_EQUIPMENT_POSITION;
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
