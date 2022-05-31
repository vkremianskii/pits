package com.github.vkremianskii.pits.communicator.amqp;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

    public static final String EXCHANGE_EQUIPMENT_POSITION = "exchange.equipment.position";
    public static final String EXCHANGE_EQUIPMENT_PAYLOAD = "exchange.equipment.payload";

    @Bean
    Declarables declarables() {
        return new Declarables(
            new FanoutExchange(EXCHANGE_EQUIPMENT_POSITION),
            new FanoutExchange(EXCHANGE_EQUIPMENT_PAYLOAD));
    }
}
