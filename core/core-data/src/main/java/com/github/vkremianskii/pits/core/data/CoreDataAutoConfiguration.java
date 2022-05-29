package com.github.vkremianskii.pits.core.data;

import io.r2dbc.spi.ConnectionFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy;

@Configuration
public class CoreDataAutoConfiguration {

    @Bean
    DSLContext dslContext(ConnectionFactory connectionFactory) {
        return DSL.using(new TransactionAwareConnectionFactoryProxy(connectionFactory), SQLDialect.POSTGRES);
    }
}
