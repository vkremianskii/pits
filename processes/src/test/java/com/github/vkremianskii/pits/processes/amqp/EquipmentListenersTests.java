package com.github.vkremianskii.pits.processes.amqp;

import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.TruckPayloadWeightRepository;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.TruckPayloadWeightChanged;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EquipmentListenersTests {
    EquipmentPositionRepository positionRepository = mock(EquipmentPositionRepository.class);
    TruckPayloadWeightRepository payloadWeightRepository = mock(TruckPayloadWeightRepository.class);
    EquipmentListeners sut = new EquipmentListeners(positionRepository, payloadWeightRepository);

    @Test
    void should_listen_to_position_and_insert_into_db() {
        // when
        sut.handleEquipmentPosition(new EquipmentPositionChanged(1, 41.1494512, -8.6107884, 86));

        // then
        verify(positionRepository).put(1, 41.1494512, -8.6107884, 86);
    }

    @Test
    void should_listen_to_payload_weight_and_insert_into_db() {
        // when
        sut.handleTruckPayloadWeight(new TruckPayloadWeightChanged(1, 10));

        // then
        verify(payloadWeightRepository).put(1, 10);
    }
}
