package com.github.vkremianskii.pits.registry.app.amqp;

import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EquipmentListenersTests {
    EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    EquipmentListeners sut = new EquipmentListeners(equipmentRepository);

    @Test
    void should_listen_to_position_changed_and_update_in_db() {
        // when
        sut.handlePositionChanged(new EquipmentPositionChanged(1, 41.1494512, -8.6107884, 86));

        // then
        verify(equipmentRepository).updateEquipmentPosition(eq(1), eq(new Position(41.1494512, -8.6107884, 86)));
    }

    @Test
    void should_listen_to_payload_changed_and_update_in_db() {
        // when
        sut.handlePayloadChanged(new EquipmentPayloadChanged(1, 10));

        // then
        verify(equipmentRepository).updateEquipmentPayload(1, 10);
    }
}
