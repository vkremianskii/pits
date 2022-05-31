package com.github.vkremianskii.pits.processes.amqp;

import com.github.vkremianskii.pits.core.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.core.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.processes.amqp.AmqpConfig.QUEUE_EQUIPMENT_PAYLOAD;
import static com.github.vkremianskii.pits.processes.amqp.AmqpConfig.QUEUE_EQUIPMENT_POSITION;
import static java.util.Objects.requireNonNull;

@Component
public class EquipmentListeners {

    private final EquipmentPositionRepository positionRepository;
    private final EquipmentPayloadRepository payloadRepository;

    public EquipmentListeners(EquipmentPositionRepository positionRepository,
                              EquipmentPayloadRepository payloadRepository) {
        this.positionRepository = requireNonNull(positionRepository);
        this.payloadRepository = requireNonNull(payloadRepository);
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_POSITION)
    void handlePositionChanged(EquipmentPositionChanged message) {
        positionRepository.insert(
                message.equipmentId(),
                message.latitude(),
                message.longitude(),
                message.elevation())
            .block();
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_PAYLOAD)
    void handlePayloadChanged(EquipmentPayloadChanged message) {
        payloadRepository.insert(
                message.equipmentId(),
                message.payload())
            .block();
    }
}
