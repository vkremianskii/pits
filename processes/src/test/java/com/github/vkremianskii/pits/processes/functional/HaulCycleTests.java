package com.github.vkremianskii.pits.processes.functional;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@SpringBootTest(properties = {"integration.registry.baseUrl=http://localhost:18080"})
@WireMockTest(httpPort = 18080)
public class HaulCycleTests {
    @Autowired
    HaulCycleRepository haulCycleRepository;
    @Autowired
    HaulCycleJob haulCycleJob;

    @BeforeEach
    void cleanup() {
        haulCycleRepository.clear().block();
    }

    @Test
    void should_compute_haul_cycles() {
        // given
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                .withBody("""
                        [{
                            "id": 1,
                            "name": "Shovel No.1",
                            "type": "SHOVEL",
                            "loadRadius": 20
                        },{
                            "id": 2,
                            "name": "Truck No.1",
                            "type": "TRUCK"
                        },{
                            "id": 3,
                            "name": "Truck No.1",
                            "type": "TRUCK"
                        }]
                        """)));
        stubFor(post(urlPathEqualTo("/equipment/2/state")).willReturn(aResponse()
                .withStatus(200)));
        haulCycleRepository.insert(2, 1, Instant.now(), null, null, null, null, null, null, null).block();

        // when
        haulCycleJob.computeHaulCycles();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/2/state"))
                .withRequestBody(equalToJson("""
                        {
                            "state": "WAIT_LOAD"
                        }
                        """)));
    }
}
