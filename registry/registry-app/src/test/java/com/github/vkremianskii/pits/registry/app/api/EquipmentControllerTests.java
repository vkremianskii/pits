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
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.github.vkremianskii.pits.registry.types.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.types.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static com.github.vkremianskii.pits.registry.types.model.equipment.TruckState.HAUL;
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
    @Autowired
    WebTestClient webClient;

    @Test
    void should_get_equipment__v1() {
        // given
        when(equipmentRepository.getEquipment())
            .thenReturn(Mono.just(List.of(
                new Dozer(1, "Dozer No.1", null, null),
                new Drill(2, "Drill No.1", null, null),
                new Shovel(3, "Shovel No.1", 20, null, null),
                new Truck(4, "Truck No.1", HAUL, new Position(41.1494512, -8.6107884, 86), 10))));

        // expect
        webClient.get()
            .uri("/equipment")
            .exchange()
            .expectStatus().isOk()
            .expectBody().json("""
                [{
                    "id": 1,
                    "name": "Dozer No.1",
                    "type": "DOZER"
                },{
                    "id": 2,
                    "name": "Drill No.1",
                    "type": "DRILL"
                },{
                    "id": 3,
                    "name": "Shovel No.1",
                    "type": "SHOVEL",
                    "loadRadius": 20
                },{
                    "id": 4,
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
                """, true);
    }

    @Test
    void should_get_equipment__v2() {
        // given
        when(equipmentRepository.getEquipment())
            .thenReturn(Mono.just(List.of(
                new Dozer(1, "Dozer No.1", null, null),
                new Drill(2, "Drill No.1", null, null),
                new Shovel(3, "Shovel No.1", 20, null, null),
                new Truck(4, "Truck No.1", HAUL, new Position(41.1494512, -8.6107884, 86), 10))));

        // expect
        webClient.get()
            .uri("/equipment")
            .header(API_VERSION, EQUIPMENT_RESPONSE_OBJECT.toString())
            .exchange()
            .expectStatus().isOk()
            .expectBody().json("""
                {
                    "equipment": [{
                        "id": 1,
                        "name": "Dozer No.1",
                        "type": "DOZER"
                    },{
                        "id": 2,
                        "name": "Drill No.1",
                        "type": "DRILL"
                    },{
                        "id": 3,
                        "name": "Shovel No.1",
                        "type": "SHOVEL",
                        "loadRadius": 20
                    },{
                        "id": 4,
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
                """, true);
    }

    @Test
    void should_create_equipment() {
        // given
        when(equipmentRepository.createEquipment("Truck No.1", TRUCK))
            .thenReturn(Mono.just(1));

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
            .expectBody().json("""
                {
                    "equipmentId": 1
                }
                """);
        verify(equipmentRepository).createEquipment("Truck No.1", TRUCK);
    }

    @Test
    void should_update_equipment_state() {
        // given
        when(equipmentRepository.getEquipmentById(1))
            .thenReturn(Mono.just(Optional.of(new Truck(1, "Truck No.1", null, null, null))));
        when(equipmentRepository.updateEquipmentState(1, TruckState.EMPTY))
            .thenReturn(Mono.empty());

        // expect
        webClient.post()
            .uri("/equipment/{id}/state", 1)
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "state": "EMPTY"
                }
                """)
            .exchange()
            .expectStatus().isOk();
        verify(equipmentRepository).updateEquipmentState(1, TruckState.EMPTY);
    }

    @Test
    void should_update_equipment_state__equipment_not_found() {
        // given
        when(equipmentRepository.getEquipmentById(1)).thenReturn(Mono.just(Optional.empty()));

        // expect
        webClient.post()
            .uri("/equipment/{id}/state", 1)
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "state": "EMPTY"
                }
                """)
            .exchange()
            .expectStatus().isEqualTo(NOT_FOUND);
        verify(equipmentRepository).getEquipmentById(1);
        verifyNoMoreInteractions(equipmentRepository);
    }

    @Test
    void should_update_equipment_state__invalid_state() {
        // given
        when(equipmentRepository.getEquipmentById(1))
            .thenReturn(Mono.just(Optional.of(new Dozer(1, "Dozer No.1", null, null))));

        // expect
        webClient.post()
            .uri("/equipment/{id}/state", 1)
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                {
                    "state": "EMPTY"
                }
                """)
            .exchange()
            .expectStatus().isEqualTo(BAD_REQUEST);
        verify(equipmentRepository).getEquipmentById(1);
        verifyNoMoreInteractions(equipmentRepository);
    }
}
