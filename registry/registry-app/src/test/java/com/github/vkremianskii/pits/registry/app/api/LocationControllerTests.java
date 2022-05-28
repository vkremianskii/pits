package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.types.model.LocationDeclaration;
import com.github.vkremianskii.pits.registry.types.model.LocationPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.STOCKPILE;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import(CoreWebAutoConfiguration.class)
class LocationControllerTests {

    @MockBean
    EquipmentRepository equipmentRepository;
    @MockBean
    LocationRepository locationRepository;
    @MockBean
    LocationPointRepository locationPointRepository;
    @Autowired
    WebTestClient webClient;

    @Test
    void should_get_locations() {
        // given
        when(locationRepository.getLocations())
            .thenReturn(Mono.just(List.of(
                new LocationDeclaration(1, "Dump No.1", DUMP),
                new LocationDeclaration(2, "Face No.1", FACE),
                new LocationDeclaration(3, "Hole No.1", HOLE),
                new LocationDeclaration(4, "Stockpile No.1", STOCKPILE))));
        when(locationPointRepository.getPointsByLocationId(1))
            .thenReturn(Mono.just(List.of(new LocationPoint(1, 1, 0, 41.1494512, -8.6107884))));
        when(locationPointRepository.getPointsByLocationId(2))
            .thenReturn(Mono.empty());
        when(locationPointRepository.getPointsByLocationId(3))
            .thenReturn(Mono.empty());
        when(locationPointRepository.getPointsByLocationId(4))
            .thenReturn(Mono.empty());

        // expect
        webClient.get()
            .uri("/location")
            .exchange()
            .expectStatus().isOk()
            .expectBody().json("""
                {
                    "locations": [{            
                        "id": 1,
                        "name": "Dump No.1",
                        "type": "DUMP",
                        "geometry": [{
                            "latitude": 41.1494512,
                            "longitude": -8.6107884
                        }]
                    },{
                        "id": 2,
                        "name": "Face No.1",
                        "type": "FACE",
                        "geometry": []
                    },{
                        "id": 3,
                        "name": "Hole No.1",
                        "type": "HOLE",
                        "geometry": []
                    },{
                        "id": 4,
                        "name": "Stockpile No.1",
                        "type": "STOCKPILE",
                        "geometry": []
                    }]
                }
                """, true);
    }
}
