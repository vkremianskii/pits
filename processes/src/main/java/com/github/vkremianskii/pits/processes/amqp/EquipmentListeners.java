package com.github.vkremianskii.pits.processes.amqp;

import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.TruckPayloadWeightRepository;
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
    void handleEquipmentPosition(String message) {
    }

    @RabbitListener(queues = QUEUE_TRUCK_PAYLOAD_WEIGHT)
    void handleTruckPayloadWeight(String message) {
    }
}
