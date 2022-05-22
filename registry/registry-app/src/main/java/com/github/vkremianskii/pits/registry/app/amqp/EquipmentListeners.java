package com.github.vkremianskii.pits.registry.app.amqp;

import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.registry.app.amqp.AmqpConfig.QUEUE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.registry.app.amqp.AmqpConfig.QUEUE_EQUIPMENT_PAYLOAD;
import static java.util.Objects.requireNonNull;

@Component
public class EquipmentListeners {
    private final EquipmentRepository equipmentRepository;

    public EquipmentListeners(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = requireNonNull(equipmentRepository);
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_POSITION)
    void handlePositionChanged(EquipmentPositionChanged message) {
        equipmentRepository.setEquipmentPosition(
                message.getEquipmentId(),
                new Position(
                        message.getLatitude(),
                        message.getLongitude(),
                        message.getElevation()));
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_PAYLOAD)
    void handlePayloadChanged(EquipmentPayloadChanged message) {
        equipmentRepository.setEquipmentPayload(
                message.getEquipmentId(),
                message.getPayload());
    }
}
