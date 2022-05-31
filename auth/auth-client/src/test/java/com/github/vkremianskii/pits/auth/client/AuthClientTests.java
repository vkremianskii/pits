package com.github.vkremianskii.pits.auth.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.vkremianskii.pits.core.json.CoreTypesModule;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WireMockTest
class AuthClientTests {

    ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
        .modules(new CoreTypesModule())
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .build();

    @Test
    void should_create_user(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlPathEqualTo("/user"))
            .withRequestBody(equalToJson("""
                {
                    "username": "username",
                    "password": "password",
                    "scopes": ["scope"]
                }
                """))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
        var sut = newClient(wmRuntimeInfo);

        // when
        var response = sut.createUser(
            username("username"),
            "password".toCharArray(),
            Set.of(scope("scope"))).block();

        // then
        verify(postRequestedFor(urlPathEqualTo("/user"))
            .withRequestBody(equalToJson("""
                {
                    "username": "username",
                    "password": "password",
                    "scopes": ["scope"]
                }
                """)));
    }

    @Test
    void should_authenticate_user(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlPathEqualTo("/user/auth"))
            .withRequestBody(equalToJson("""
                {
                    "username": "username",
                    "password": "password"
                }
                """))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())));
        var sut = newClient(wmRuntimeInfo);

        // when
        var response = sut.authenticate(username("username"), "password".toCharArray()).block();

        // then
        verify(postRequestedFor(urlPathEqualTo("/user/auth"))
            .withRequestBody(equalToJson("""
                {
                    "username": "username",
                    "password": "password"
                }
                """)));
    }

    AuthClient newClient(WireMockRuntimeInfo wmRuntimeInfo) {
        var baseUrl = "http://localhost:" + wmRuntimeInfo.getHttpPort();
        var properties = new AuthProperties(baseUrl);
        return new AuthClient(properties, objectMapper);
    }
}
