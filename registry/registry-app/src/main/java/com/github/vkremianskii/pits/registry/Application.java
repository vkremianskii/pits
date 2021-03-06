package com.github.vkremianskii.pits.registry;

import com.github.vkremianskii.pits.auth.client.security.AuthAppSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = MongoReactiveAutoConfiguration.class)
@Import(AuthAppSecurityConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
