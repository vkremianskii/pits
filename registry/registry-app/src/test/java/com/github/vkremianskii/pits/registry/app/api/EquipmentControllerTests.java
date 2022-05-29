package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.core.types.model.Position;
import com.github.vkremianskii.pits.core.types.model.equipment.Truck;
import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.github.vkremianskii.pits.core.types.TestEquipment.aDozer;
import static com.github.vkremianskii.pits.core.types.TestEquipment.aDrill;
import static com.github.vkremianskii.pits.core.types.TestEquipment.aShovel;
import static com.github.vkremianskii.pits.core.types.TestEquipment.aTruck;
import static com.github.vkremianskii.pits.core.types.TestEquipment.randomEquipmentId;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.TRUCK;
import static com.github.vkremianskii.pits.registry.types.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.types.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = EquipmentController.class)
@Import(CoreWebAutoConfiguration.class)
class EquipmentControllerTests {

    @MockBean
    EquipmentRepository equipmentRepository;
    @Autowired
    WebTestClient webClient;

    @Test
    void should_get_equipment__v1() {
        // given
        var dozer = aDozer();
        var drill = aDrill();
        var shovel = aShovel();
        var truck = aTruck(Truck.STATE_HAUL, new Position(41.1494512, -8.6107884, 86), 10);
        when(equipmentRepository.getEquipment())
            .thenReturn(Mono.just(List.of(dozer, drill, shovel, truck)));

        // expect
        webClient.get()
            .uri("/equipment")
            .exchange()
            .expectStatus().isOk()
            .expectBody().json(String.format("""
                [{
                    "id": "%s",
                    "name": "%s",
                    "type": "DOZER"
                },{
                    "id": "%s",
                    "name": "%s",
                    "type": "DRILL"
                },{
                    "id": "%s",
                    "name": "%s",
                    "type": "SHOVEL",
                    "loadRadius": 20
                },{
                    "id": "%s",
                    "name": "%s",
                    "type": "TRUCK",
                    "state": "HAUL",
                    "position": {
                        "latitude": 41.1494512,
                        "longitude": -8.6107884,
                        "elevation": 86
                    },
                    "payload": 10
                }]
                """, dozer.id, dozer.name, drill.id, drill.name, shovel.id, shovel.name, truck.id, truck.name));
    }

    @Test
    void should_get_equipment__v2() {
        // given
        var dozer = aDozer();
        var drill = aDrill();
        var shovel = aShovel();
        var truck = aTruck(Truck.STATE_HAUL, new Position(41.1494512, -8.6107884, 86), 10);
        when(equipmentRepository.getEquipment())
            .thenReturn(Mono.just(List.of(dozer, drill, shovel, truck)));

        // expect
        webClient.get()
            .uri("/equipment")
            .header(API_VERSION, EQUIPMENT_RESPONSE_OBJECT.toString())
            .exchange()
            .expectStatus().isOk()
            .expectBody().json(String.format("""
                {
                    "equipment": [{
                        "id": "%s",
                        "name": "%s",
                        "type": "DOZER"
                    },{
                        "id": "%s",
                        "name": "%s",
                        "type": "DRILL"
                    },{
                        "id": "%s",
                        "name": "%s",
                        "type": "SHOVEL",
                        "loadRadius": 20
                    },{
                        "id": "%s",
                        "name": "%s",
                        "type": "TRUCK",
                        "state": "HAUL",
                        "position": {
                            "latitude": 41.1494512,
                            "longitude": -8.6107884,
                            "elevation": 86
                        },
                        "payload": 10
                    }]
                }
                """, dozer.id, dozer.name, drill.id, drill.name, shovel.id, shovel.name, truck.id, truck.name));
    }

    @Test
    void should_create_equipment() {
        // given
        when(equipmentRepository.createEquipment(any(), eq("Truck No.1"), eq(TRUCK), isNull()))
            .thenReturn(Mono.empty());

        // expect
        webClient.post()
            .uri("/equipment")
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "name": "Truck No.1",
                    "type": "TRUCK"
                }
                """)
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.equipmentId").isNotEmpty();
        verify(equipmentRepository).createEquipment(any(), eq("Truck No.1"), eq(TRUCK), isNull());
    }

    @Test
    void should_update_equipment_state() {
        // given
        var truck = aTruck();
        when(equipmentRepository.getEquipmentById(truck.id))
            .thenReturn(Mono.just(Optional.of(truck)));
        when(equipmentRepository.updateEquipmentState(truck.id, Truck.STATE_EMPTY))
            .thenReturn(Mono.empty());

        // expect
        webClient.post()
            .uri("/equipment/{id}/state", truck.id)
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "state": "EMPTY"
                }
                """)
            .exchange()
            .expectStatus().isOk();
        verify(equipmentRepository).updateEquipmentState(truck.id, Truck.STATE_EMPTY);
    }

    @Test
    void should_update_equipment_state__equipment_not_found() {
        // given
        var truckId = randomEquipmentId();
        when(equipmentRepository.getEquipmentById(truckId))
            .thenReturn(Mono.just(Optional.empty()));

        // expect
        webClient.post()
            .uri("/equipment/{id}/state", truckId)
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "state": "EMPTY"
                }
                """)
            .exchange()
            .expectStatus().isEqualTo(NOT_FOUND);
        verify(equipmentRepository).getEquipmentById(truckId);
        verifyNoMoreInteractions(equipmentRepository);
    }

    @Test
    void should_update_equipment_state__invalid_state() {
        // given
        var dozer = aDozer();
        when(equipmentRepository.getEquipmentById(dozer.id))
            .thenReturn(Mono.just(Optional.of(dozer)));

        // expect
        webClient.post()
            .uri("/equipment/{id}/state", dozer.id)
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "state": "EMPTY"
                }
                """)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST);
        verify(equipmentRepository).getEquipmentById(dozer.id);
        verifyNoMoreInteractions(equipmentRepository);
    }
}
