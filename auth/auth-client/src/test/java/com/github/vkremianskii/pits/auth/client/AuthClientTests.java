package com.github.vkremianskii.pits.auth.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.vkremianskii.pits.auth.dto.AuthenticateResponse;
import com.github.vkremianskii.pits.auth.model.UserId;
import com.github.vkremianskii.pits.core.json.CoreModule;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WireMockTest
class AuthClientTests {

    ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
        .modules(new CoreModule())
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
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                .withBody("""
                    {
                        "userId": "f5c9e539-008f-4344-bcc4-0673f22ce8b5"
                    }
                    """)));
        var sut = newClient(wmRuntimeInfo);

        // when
        var response = sut.createUser(
            username("username"),
            "password".toCharArray(),
            Set.of(scope("scope"))).block();

        // then
        assertThat(response.userId()).isEqualTo(UserId.valueOf("f5c9e539-008f-4344-bcc4-0673f22ce8b5"));
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
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                .withBody("""
                    {
                        "userId": "d7a8ba56-c335-4035-b781-942b4052c37e",
                        "scopes": ["scope"]
                    }
                    """)));
        var sut = newClient(wmRuntimeInfo);

        // when
        var response = sut.authenticateUser(username("username"), "password".toCharArray()).block();

        // then
        assertThat(response).isEqualTo(new AuthenticateResponse(
            UserId.valueOf("d7a8ba56-c335-4035-b781-942b4052c37e"),
            Set.of(scope("scope"))));
        verify(postRequestedFor(urlPathEqualTo("/user/auth"))
            .withRequestBody(equalToJson("""
                {
                    "username": "username",
                    "password": "password"
                }
                """)));
    }

    @Test
    void should_authenticate_user__unauthorized(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(post(urlPathEqualTo("/user/auth"))
            .withRequestBody(equalToJson("""
                {
                    "username": "username",
                    "password": "password"
                }
                """))
            .willReturn(aResponse()
                .withStatus(401)));
        var sut = newClient(wmRuntimeInfo);

        // when
        assertThatThrownBy(() -> sut.authenticateUser(
            username("username"),
            "password".toCharArray()).block()).isInstanceOf(WebClientResponseException.Unauthorized.class);

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
