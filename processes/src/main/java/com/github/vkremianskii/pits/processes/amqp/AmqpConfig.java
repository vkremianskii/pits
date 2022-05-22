package com.github.vkremianskii.pits.processes.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

@Configuration
public class AmqpConfig {
    public static final String QUEUE_EQUIPMENT_POSITION = "processes.queue.equipment.position";
    public static final String QUEUE_EQUIPMENT_PAYLOAD = "processes.queue.equipment.payload";
    private static final String EXCHANGE_EQUIPMENT_POSITION = "exchange.equipment.position";
    private static final String EXCHANGE_EQUIPMENT_PAYLOAD = "exchange.equipment.payload";

    @Bean
    Declarables declarables() {
        return new Declarables(
                new Queue(QUEUE_EQUIPMENT_POSITION),
                new Queue(QUEUE_EQUIPMENT_PAYLOAD),
                new FanoutExchange(EXCHANGE_EQUIPMENT_POSITION),
                new FanoutExchange(EXCHANGE_EQUIPMENT_PAYLOAD),
                new Binding(QUEUE_EQUIPMENT_POSITION, QUEUE, EXCHANGE_EQUIPMENT_POSITION, "", null),
                new Binding(QUEUE_EQUIPMENT_PAYLOAD, QUEUE, EXCHANGE_EQUIPMENT_PAYLOAD, "", null));
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
