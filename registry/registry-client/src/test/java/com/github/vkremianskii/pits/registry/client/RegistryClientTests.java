package com.github.vkremianskii.pits.registry.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
class RegistryClientTests {

    @Test
    void should_update_equipment_position(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlPathEqualTo("/equipment/1/position"))
                .willReturn(aResponse().withStatus(200)));
        var webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wmRuntimeInfo.getHttpPort())
                .build();
        var sut = new RegistryClient(webClient);

        // when
        sut.updateEquipmentPosition(1, 41.1494512, -8.6107884, 86).block();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/1/position"))
                .withRequestBody(equalToJson("" +
                        "{" +
                        "  \"latitude\": 41.1494512," +
                        "  \"longitude\": -8.6107884," +
                        "  \"elevation\": 86" +
                        "}")));
    }

    @Test
    void should_update_truck_payload_weight(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlPathEqualTo("/equipment/1/payload/weight"))
                .willReturn(aResponse().withStatus(200)));
        var webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wmRuntimeInfo.getHttpPort())
                .build();
        var sut = new RegistryClient(webClient);

        // when
        sut.updateTruckPayloadWeight(1, 10).block();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/1/payload/weight"))
                .withRequestBody(equalToJson("" +
                        "{" +
                        "  \"weight\": 10" +
                        "}")));
    }
}
