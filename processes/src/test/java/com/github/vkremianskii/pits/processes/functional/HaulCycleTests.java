package com.github.vkremianskii.pits.processes.functional;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.HaulCycleRepository;
import com.github.vkremianskii.pits.processes.job.HaulCycleJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.vkremianskii.pits.core.types.TestEquipment.randomEquipmentId;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@SpringBootTest(properties = {"integration.registry.baseUrl=http://localhost:18080"})
@WireMockTest(httpPort = 18080)
public class HaulCycleTests {

    @Autowired
    EquipmentPositionRepository positionRepository;
    @Autowired
    EquipmentPayloadRepository payloadRepository;
    @Autowired
    HaulCycleRepository haulCycleRepository;
    @Autowired
    HaulCycleJob haulCycleJob;

    @BeforeEach
    void cleanup() {
        positionRepository.clear().block();
        payloadRepository.clear().block();
        haulCycleRepository.clear().block();
    }

    @Test
    void should_compute_haul_cycles() {
        // given
        var truckId = randomEquipmentId();
        var shovelId = randomEquipmentId();
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
            .withStatus(200)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
            .withBody(String.format("""
                {
                    "equipment": [{
                        "id": "%s",
                        "name": "Shovel No.1",
                        "type": "SHOVEL",
                        "loadRadius": 20
                    },{
                        "id": "%s",
                        "name": "Truck No.1",
                        "type": "TRUCK"
                    }]
                }
                """, shovelId, truckId))));
        stubFor(post(urlPathEqualTo("/equipment/" + truckId + "/state")).willReturn(aResponse()
            .withStatus(200)));
        haulCycleRepository.insert(truckId, shovelId, Instant.now(), null, null, null, null, null, null, null).block();

        // when
        haulCycleJob.computeHaulCycles();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/" + truckId + "/state"))
            .withRequestBody(equalToJson("""
                {
                    "state": "WAIT_LOAD"
                }
                """)));
    }

    @Test
    void should_compute_haul_cycles__rollback_on_error() {
        // given
        var truckId = randomEquipmentId();
        var shovelId = randomEquipmentId();
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
            .withStatus(200)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
            .withBody(String.format("""
                {
                    "equipment": [{
                        "id": "%s",
                        "name": "Shovel No.1",
                        "type": "SHOVEL",
                        "loadRadius": 20
                    },{
                        "id": "%s",
                        "name": "Truck No.1",
                        "type": "TRUCK"
                    }]
                }
                """, shovelId, truckId))));
        stubFor(post(urlPathEqualTo("/equipment/" + truckId + "/state")).willReturn(aResponse()
            .withStatus(500)));
        payloadRepository.insert(truckId, 20_000, Instant.ofEpochSecond(1)).block();

        // when
        haulCycleJob.computeHaulCycles();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/" + truckId + "/state"))
            .withRequestBody(equalToJson("""
                {
                    "state": "LOAD"
                }
                """)));
        var haulCycles = haulCycleRepository.getLastHaulCycleForTruck(truckId).block();
        assertThat(haulCycles).isEmpty();
    }
}
