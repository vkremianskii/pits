package com.github.vkremianskii.pits.processes.amqp;

import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.TruckPayloadWeightRepository;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.TruckPayloadWeightChanged;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.processes.amqp.AmqpConfig.QUEUE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.processes.amqp.AmqpConfig.QUEUE_TRUCK_PAYLOAD_WEIGHT;
import static java.util.Objects.requireNonNull;

@Component
public class EquipmentListeners {
    private final EquipmentPositionRepository positionRepository;
    private final TruckPayloadWeightRepository payloadWeightRepository;

    public EquipmentListeners(EquipmentPositionRepository positionRepository,
                              TruckPayloadWeightRepository payloadWeightRepository) {
        this.positionRepository = requireNonNull(positionRepository);
        this.payloadWeightRepository = requireNonNull(payloadWeightRepository);
    }

    @RabbitListener(queues = QUEUE_EQUIPMENT_POSITION)
    void handleEquipmentPosition(EquipmentPositionChanged message) {
        positionRepository.put(
                message.getEquipmentId(),
                message.getLatitude(),
                message.getLongitude(),
                message.getElevation());
    }

    @RabbitListener(queues = QUEUE_TRUCK_PAYLOAD_WEIGHT)
    void handleTruckPayloadWeight(TruckPayloadWeightChanged message) {
        payloadWeightRepository.put(
                message.getEquipmentId(),
                message.getWeight());
    }
}
