package com.github.vkremianskii.pits.registry.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.registry")
public record RegistryProperties(String baseUrl,
                                 String username,
                                 String password) {

}
