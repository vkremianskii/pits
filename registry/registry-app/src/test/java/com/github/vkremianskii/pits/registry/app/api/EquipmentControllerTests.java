package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.github.vkremianskii.pits.registry.types.model.equipment.TruckState.HAUL;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Mono.just;

@WebFluxTest
class EquipmentControllerTests {
    @MockBean
    EquipmentRepository equipmentRepository;
    @MockBean
    LocationRepository locationRepository;
    @Autowired
    WebTestClient webClient;

    @Test
    void should_get_equipment() {
        // given
        when(equipmentRepository.getEquipment())
                .thenReturn(just(List.of(
                        new Dozer(1, "Dozer No.1", null, null),
                        new Drill(2, "Drill No.1", null, null),
                        new Shovel(3, "Shovel No.1", null, null),
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
                            "type": "SHOVEL"
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
                            "payloadWeight": 10
                        }]
                        """, true);
    }
}
