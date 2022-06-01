package com.github.vkremianskii.pits.auth.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.auth")
public record AuthProperties(String baseUrl,
                             String username,
                             String password) {

}
