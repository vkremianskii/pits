package com.github.vkremianskii.pits.processes.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

@Configuration
public class AmqpConfig {
    public static final String QUEUE_EQUIPMENT_POSITION = "processes.queue.equipment.position";
    public static final String QUEUE_TRUCK_PAYLOAD_WEIGHT = "processes.queue.truck.payload.weight";
    private static final String EXCHANGE_EQUIPMENT_POSITION = "exchange.equipment.position";
    private static final String EXCHANGE_TRUCK_PAYLOAD_WEIGHT = "exchange.truck.payload.weight";

    @Bean
    Declarables declarables() {
        return new Declarables(
                new Queue(QUEUE_EQUIPMENT_POSITION),
                new Queue(QUEUE_TRUCK_PAYLOAD_WEIGHT),
                new FanoutExchange(EXCHANGE_EQUIPMENT_POSITION),
                new FanoutExchange(EXCHANGE_TRUCK_PAYLOAD_WEIGHT),
                new Binding(QUEUE_EQUIPMENT_POSITION, QUEUE, EXCHANGE_EQUIPMENT_POSITION, "", null),
                new Binding(QUEUE_TRUCK_PAYLOAD_WEIGHT, QUEUE, EXCHANGE_TRUCK_PAYLOAD_WEIGHT, "", null));
    }
}
