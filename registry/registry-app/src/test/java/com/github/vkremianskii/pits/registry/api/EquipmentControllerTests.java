package com.github.vkremianskii.pits.registry.api;

import com.github.vkremianskii.pits.auth.client.AuthClient;
import com.github.vkremianskii.pits.auth.dto.AuthenticateResponse;
import com.github.vkremianskii.pits.core.model.Position;
import com.github.vkremianskii.pits.core.model.equipment.Truck;
import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.registry.data.EquipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.core.TestEquipment.aDozer;
import static com.github.vkremianskii.pits.core.TestEquipment.aDrill;
import static com.github.vkremianskii.pits.core.TestEquipment.aShovel;
import static com.github.vkremianskii.pits.core.TestEquipment.aTruck;
import static com.github.vkremianskii.pits.core.TestEquipment.randomEquipmentId;
import static com.github.vkremianskii.pits.core.model.EquipmentType.TRUCK;
import static com.github.vkremianskii.pits.registry.ApiHeaders.API_VERSION;
import static com.github.vkremianskii.pits.registry.ApiVersion.EQUIPMENT_RESPONSE_OBJECT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = EquipmentController.class)
@Import(CoreWebAutoConfiguration.class)
class EquipmentControllerTests {

    @MockBean
    AuthClient authClient;
    @MockBean
    EquipmentRepository equipmentRepository;
    @Autowired
    WebTestClient webClient;

    @BeforeEach
    void setup() {
        when(authClient.authenticate(username("admin"), "admin".toCharArray()))
            .thenReturn(Mono.just(new AuthenticateResponse(Set.of(scope("scope")))));
    }

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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
