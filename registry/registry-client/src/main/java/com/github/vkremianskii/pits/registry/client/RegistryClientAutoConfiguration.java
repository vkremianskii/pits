package com.github.vkremianskii.pits.registry.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RegistryClient.class)
@EnableConfigurationProperties(RegistryProperties.class)
public class RegistryClientAutoConfiguration {

}
