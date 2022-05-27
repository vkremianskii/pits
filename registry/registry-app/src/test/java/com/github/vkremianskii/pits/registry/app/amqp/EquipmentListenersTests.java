package com.github.vkremianskii.pits.registry.app.amqp;

import com.github.vkremianskii.pits.core.types.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.core.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EquipmentListenersTests {

    EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    EquipmentListeners sut = new EquipmentListeners(equipmentRepository);

    @Test
    void should_listen_to_position_changed_and_update_in_db() {
        // given
        when(equipmentRepository.updateEquipmentPosition(1, new Position(41.1494512, -8.6107884, 86)))
            .thenReturn(Mono.empty());

        // when
        sut.handlePositionChanged(new EquipmentPositionChanged(1, 41.1494512, -8.6107884, 86));

        // then
        verify(equipmentRepository).updateEquipmentPosition(eq(1), eq(new Position(41.1494512, -8.6107884, 86)));
    }

    @Test
    void should_listen_to_payload_changed_and_update_in_db() {
        // given
        when(equipmentRepository.updateEquipmentPayload(1, 10))
            .thenReturn(Mono.empty());

        // when
        sut.handlePayloadChanged(new EquipmentPayloadChanged(1, 10));

        // then
        verify(equipmentRepository).updateEquipmentPayload(1, 10);
    }
}
