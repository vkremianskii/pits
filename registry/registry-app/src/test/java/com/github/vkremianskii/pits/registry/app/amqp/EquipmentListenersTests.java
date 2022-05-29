package com.github.vkremianskii.pits.registry.app.amqp;

import com.github.vkremianskii.pits.core.types.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.core.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.core.types.model.Position;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.core.types.TestEquipment.randomEquipmentId;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EquipmentListenersTests {

    EquipmentRepository equipmentRepository = mock(EquipmentRepository.class);
    EquipmentListeners sut = new EquipmentListeners(equipmentRepository);

    @Test
    void should_listen_to_position_changed_and_update_in_db() {
        // given
        var equipmentId = randomEquipmentId();
        when(equipmentRepository.updateEquipmentPosition(equipmentId, new Position(41.1494512, -8.6107884, 86)))
            .thenReturn(Mono.empty());

        // when
        sut.handlePositionChanged(new EquipmentPositionChanged(equipmentId, 41.1494512, -8.6107884, 86));

        // then
        verify(equipmentRepository).updateEquipmentPosition(eq(equipmentId), eq(new Position(41.1494512, -8.6107884, 86)));
    }

    @Test
    void should_listen_to_payload_changed_and_update_in_db() {
        // given
        var equipmentId = randomEquipmentId();
        when(equipmentRepository.updateEquipmentPayload(equipmentId, 10))
            .thenReturn(Mono.empty());

        // when
        sut.handlePayloadChanged(new EquipmentPayloadChanged(equipmentId, 10));

        // then
        verify(equipmentRepository).updateEquipmentPayload(equipmentId, 10);
    }
}
