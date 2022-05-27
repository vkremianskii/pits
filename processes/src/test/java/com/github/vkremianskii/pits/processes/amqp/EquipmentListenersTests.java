package com.github.vkremianskii.pits.processes.amqp;

import com.github.vkremianskii.pits.core.types.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.core.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EquipmentListenersTests {

    EquipmentPositionRepository positionRepository = mock(EquipmentPositionRepository.class);
    EquipmentPayloadRepository payloadRepository = mock(EquipmentPayloadRepository.class);
    EquipmentListeners sut = new EquipmentListeners(positionRepository, payloadRepository);

    @Test
    void should_listen_to_position_changed_and_insert_into_db() {
        // given
        when(positionRepository.insert(1, 41.1494512, -8.6107884, 86))
            .thenReturn(Mono.empty());

        // when
        sut.handlePositionChanged(new EquipmentPositionChanged(1, 41.1494512, -8.6107884, 86));

        // then
        verify(positionRepository).insert(1, 41.1494512, -8.6107884, 86);
    }

    @Test
    void should_listen_to_payload_changed_and_insert_into_db() {
        // given
        when(payloadRepository.insert(1, 10))
            .thenReturn(Mono.empty());

        // when
        sut.handlePayloadChanged(new EquipmentPayloadChanged(1, 10));

        // then
        verify(payloadRepository).insert(1, 10);
    }
}
