package com.github.vkremianskii.pits.processes.amqp;

import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPayloadChanged;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EquipmentListenersTests {
    EquipmentPositionRepository positionRepository = mock(EquipmentPositionRepository.class);
    EquipmentPayloadRepository payloadRepository = mock(EquipmentPayloadRepository.class);
    EquipmentListeners sut = new EquipmentListeners(positionRepository, payloadRepository);

    @Test
    void should_listen_to_position_changed_and_insert_into_db() {
        // when
        sut.handlePositionChanged(new EquipmentPositionChanged(1, 41.1494512, -8.6107884, 86));

        // then
        verify(positionRepository).insert(1, 41.1494512, -8.6107884, 86);
    }

    @Test
    void should_listen_to_payload_changed_and_insert_into_db() {
        // when
        sut.handlePayloadChanged(new EquipmentPayloadChanged(1, 10));

        // then
        verify(payloadRepository).insert(1, 10);
    }
}
