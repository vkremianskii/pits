package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentId.equipmentId;
import static com.github.vkremianskii.pits.registry.types.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.types.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static com.github.vkremianskii.pits.registry.types.model.equipment.TruckState.HAUL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest
@Import(CoreWebAutoConfiguration.class)
class EquipmentControllerTests {

    @MockBean
    EquipmentRepository equipmentRepository;
    @MockBean
    LocationRepository locationRepository;
    @MockBean
    LocationPointRepository locationPointRepository;
    @MockBean
    PlatformTransactionManager transactionManager;
    @Autowired
    WebTestClient webClient;

    @Test
    void should_get_equipment__v1() {
        // given
        var dozerId = equipmentId(UUID.randomUUID());
        var drillId = equipmentId(UUID.randomUUID());
        var shovelId = equipmentId(UUID.randomUUID());
        var truckId = equipmentId(UUID.randomUUID());
        when(equipmentRepository.getEquipment())
            .thenReturn(Mono.just(List.of(
                new Dozer(dozerId, "Dozer No.1", null, null),
                new Drill(drillId, "Drill No.1", null, null),
                new Shovel(shovelId, "Shovel No.1", 20, null, null),
                new Truck(truckId, "Truck No.1", HAUL, new Position(41.1494512, -8.6107884, 86), 10))));

        // expect
        webClient.get()
            .uri("/equipment")
            .exchange()
            .expectStatus().isOk()
            .expectBody().json("""
                [{
                    "name": "Dozer No.1",
                    "type": "DOZER"
                },{
                    "name": "Drill No.1",
                    "type": "DRILL"
                },{
                    "name": "Shovel No.1",
                    "type": "SHOVEL",
                    "loadRadius": 20
                },{
                    "name": "Truck No.1",
                    "type": "TRUCK",
                    "state": "HAUL",
                    "position": {
                        "latitude": 41.1494512,
                        "longitude": -8.6107884,
                        "elevation": 86
                    },
                    "payload": 10
                }]
                """);
    }

    @Test
    void should_get_equipment__v2() {
        // given
        var dozerId = equipmentId(UUID.randomUUID());
        var drillId = equipmentId(UUID.randomUUID());
        var shovelId = equipmentId(UUID.randomUUID());
        var truckId = equipmentId(UUID.randomUUID());
        when(equipmentRepository.getEquipment())
            .thenReturn(Mono.just(List.of(
                new Dozer(dozerId, "Dozer No.1", null, null),
                new Drill(drillId, "Drill No.1", null, null),
                new Shovel(shovelId, "Shovel No.1", 20, null, null),
                new Truck(truckId, "Truck No.1", HAUL, new Position(41.1494512, -8.6107884, 86), 10))));

        // expect
        webClient.get()
            .uri("/equipment")
            .header(API_VERSION, EQUIPMENT_RESPONSE_OBJECT.toString())
            .exchange()
            .expectStatus().isOk()
            .expectBody().json("""
                {
                    "equipment": [{
                        "name": "Dozer No.1",
                        "type": "DOZER"
                    },{
                        "name": "Drill No.1",
                        "type": "DRILL"
                    },{
                        "name": "Shovel No.1",
                        "type": "SHOVEL",
                        "loadRadius": 20
                    },{
                        "name": "Truck No.1",
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
                """);
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
        verify(transactionManager).commit(any());
    }

    @Test
    void should_create_equipment__rollback_on_error() {
        // given
        when(equipmentRepository.createEquipment(any(), eq("Truck No.1"), eq(TRUCK), isNull()))
            .thenReturn(Mono.error(new RuntimeException()));

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
            .expectStatus().isEqualTo(500);
        verify(equipmentRepository).createEquipment(any(), eq("Truck No.1"), eq(TRUCK), isNull());
        verify(transactionManager).rollback(any());
    }

    @Test
    void should_update_equipment_state() {
        // given
        var truckId = equipmentId(UUID.randomUUID());
        when(equipmentRepository.getEquipmentById(truckId))
            .thenReturn(Mono.just(Optional.of(new Truck(truckId, "Truck No.1", null, null, null))));
        when(equipmentRepository.updateEquipmentState(truckId, TruckState.EMPTY))
            .thenReturn(Mono.empty());

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
            .expectStatus().isOk();
        verify(equipmentRepository).updateEquipmentState(truckId, TruckState.EMPTY);
    }

    @Test
    void should_update_equipment_state__equipment_not_found() {
        // given
        var truckId = equipmentId(UUID.randomUUID());
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
        var dozerId = equipmentId(UUID.randomUUID());
        when(equipmentRepository.getEquipmentById(dozerId))
            .thenReturn(Mono.just(Optional.of(new Dozer(dozerId, "Dozer No.1", null, null))));

        // expect
        webClient.post()
            .uri("/equipment/{id}/state", dozerId)
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "state": "EMPTY"
                }
                """)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST);
        verify(equipmentRepository).getEquipmentById(dozerId);
        verifyNoMoreInteractions(equipmentRepository);
    }
}
