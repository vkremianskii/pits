package com.github.vkremianskii.pits.communicator.app.amqp;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                  Jackson2JsonMessageConverter jsonMessageConverter) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
