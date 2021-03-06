package com.github.vkremianskii.pits.processes;

import com.github.vkremianskii.pits.auth.client.security.AuthAppSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = MongoReactiveAutoConfiguration.class)
@EnableScheduling
@Import(AuthAppSecurityConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
