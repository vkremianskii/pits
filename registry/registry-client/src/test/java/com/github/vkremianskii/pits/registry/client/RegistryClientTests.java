package com.github.vkremianskii.pits.registry.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.vkremianskii.pits.registry.types.dto.CreateEquipmentResponse;
import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.STOCKPILE;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@WireMockTest
class RegistryClientTests {

    RegistryCodecConfigurer registryCodecConfigurer = new RegistryCodecConfigurer(new Jackson2ObjectMapperBuilder());

    @Test
    void should_get_equipment(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
            .withStatus(200)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
            .withBody("""
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
                """)));
        var sut = newClient(wmRuntimeInfo);

        // when
        var equipment = sut.getEquipment().block();

        // then
        assertThat(equipment).hasSize(4);
        assertThat(equipment).hasAtLeastOneElementOfType(Dozer.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Drill.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Shovel.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Truck.class);
        verify(getRequestedFor(urlPathEqualTo("/equipment")));
    }

    @Test
    void should_create_equipment(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlPathEqualTo("/equipment"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                .withBody("""
                    {
                        "equipmentId": 1
                    }
                    """)));
        var sut = newClient(wmRuntimeInfo);

        // when
        var response = sut.createEquipment("Truck No.1", TRUCK).block();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment"))
            .withRequestBody(equalToJson("""
                {
                    "name": "Truck No.1",
                    "type": "TRUCK"
                }
                """)));
        assertThat(response).isEqualTo(new CreateEquipmentResponse(1));
    }

    @Test
    void should_update_equipment_state(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlPathEqualTo("/equipment/1/state"))
            .willReturn(aResponse().withStatus(200)));
        var sut = newClient(wmRuntimeInfo);

        // when
        sut.updateEquipmentState(1, TruckState.EMPTY).block();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/1/state"))
            .withRequestBody(equalToJson("""
                {
                    "state": "EMPTY"
                }
                """)));
    }

    @Test
    void should_get_locations(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(urlPathEqualTo("/locations")).willReturn(aResponse()
            .withStatus(200)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
            .withBody("""
                {
                    "locations": [{
                        "id": 1,
                        "name": "Dump No.1",
                        "type": "DUMP",
                        "points": [{
                            "latitude": 41.1494512,
                            "longitude": -8.6107884
                        }]
                    },{
                        "id": 2,
                        "name": "Face No.1",
                        "type": "FACE",
                        "points": []
                    },{
                        "id": 3,
                        "name": "Hole No.1",
                        "type": "HOLE",
                        "points": []
                    },{
                        "id": 4,
                        "name": "Stockpile No.1",
                        "type": "STOCKPILE",
                        "points": []
                    }]
                }
                """)));
        var sut = newClient(wmRuntimeInfo);

        // when
        var response = sut.getLocations().block();

        // then
        var locations = response.locations();
        assertThat(locations).hasSize(4);
        var location1 = locations.get(0);
        assertThat(location1.type()).isEqualTo(DUMP);
        var location2 = locations.get(1);
        assertThat(location2.type()).isEqualTo(FACE);
        var location3 = locations.get(2);
        assertThat(location3.type()).isEqualTo(HOLE);
        var location4 = locations.get(3);
        assertThat(location4.type()).isEqualTo(STOCKPILE);
        verify(getRequestedFor(urlPathEqualTo("/locations")));
    }

    RegistryClient newClient(WireMockRuntimeInfo wmRuntimeInfo) {
        var baseUrl = "http://localhost:" + wmRuntimeInfo.getHttpPort();
        return new RegistryClient(baseUrl, registryCodecConfigurer);
    }
}
