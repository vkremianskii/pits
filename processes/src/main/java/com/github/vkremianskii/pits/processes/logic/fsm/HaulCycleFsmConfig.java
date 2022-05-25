package com.github.vkremianskii.pits.processes.logic.fsm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HaulCycleFsmConfig {

    @Bean
    HaulCycleFsmFactory haulCycleFsmFactory() {
        return new HaulCycleFsmFactory();
    }
}
