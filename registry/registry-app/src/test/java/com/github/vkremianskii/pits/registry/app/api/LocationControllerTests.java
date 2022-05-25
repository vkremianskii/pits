package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.types.model.location.Dump;
import com.github.vkremianskii.pits.registry.types.model.location.Face;
import com.github.vkremianskii.pits.registry.types.model.location.Hole;
import com.github.vkremianskii.pits.registry.types.model.location.Stockpile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest
@Import(CoreWebAutoConfiguration.class)
class LocationControllerTests {
    @MockBean
    EquipmentRepository equipmentRepository;
    @MockBean
    LocationRepository locationRepository;
    @Autowired
    WebTestClient webClient;

    @Test
    void should_get_locations() {
        // given
        when(locationRepository.getLocations())
                .thenReturn(Mono.just(List.of(
                        new Dump(1, "Dump No.1"),
                        new Face(2, "Face No.1"),
                        new Hole(3, "Hole No.1"),
                        new Stockpile(4, "Stockpile No.1"))));

        // expect
        webClient.get()
                .uri("/location")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("""
                        [{
                            "id": 1,
                            "name": "Dump No.1",
                            "type": "DUMP"
                        },{
                            "id": 2,
                            "name": "Face No.1",
                            "type": "FACE"
                        },{
                            "id": 3,
                            "name": "Hole No.1",
                            "type": "HOLE"
                        },{
                            "id": 4,
                            "name": "Stockpile No.1",
                            "type": "STOCKPILE"
                        }]
                        """, true);
    }
}
