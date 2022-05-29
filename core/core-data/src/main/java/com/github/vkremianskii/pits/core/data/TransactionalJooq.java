package com.github.vkremianskii.pits.core.data;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.r2dbc.connection.ConnectionHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Component
public class TransactionalJooq {

    private final ConnectionFactory connectionFactory;

    public TransactionalJooq(ConnectionFactory connectionFactory) {
        this.connectionFactory = requireNonNull(connectionFactory);
    }

    public <T> Mono<T> inTransactionalContext(Function<DSLContext, Mono<T>> block) {
        return Mono.usingWhen(
            getConnection(),
            (Connection con) -> block.apply(dslContext(con)),
            (Connection con) -> release(con)
        );
    }

    private Mono<Connection> getConnection() {
        return TransactionSynchronizationManager.forCurrentTransaction().flatMap(synchronizationManager -> {
                final var conHolder = (ConnectionHolder) synchronizationManager.getResource(connectionFactory);
                if (conHolder != null) {
                    final var con = getHolderConnection(conHolder);
                    if (con != null) {
                        return Mono.just(con);
                    }
                }
                return Mono.<Connection>from(connectionFactory.create());
            })
            .onErrorResume(NoTransactionException.class, __ -> Mono.from(connectionFactory.create()));
    }

    private DSLContext dslContext(Connection connection) {
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    private Mono<Void> release(Connection connection) {
        return TransactionSynchronizationManager.forCurrentTransaction().flatMap(synchronizationManager -> {
                final var conHolder = (ConnectionHolder) synchronizationManager.getResource(connectionFactory);
                if (conHolder != null) {
                    final var con = getHolderConnection(conHolder);
                    if (con != null) {
                        return Mono.empty();
                    }
                }
                return Mono.from(connection.close());
            })
            .onErrorResume(NoTransactionException.class, __ -> Mono.from(connection.close()));
    }

    private static Connection getHolderConnection(ConnectionHolder holder) {
        // Ugly, but hasConnection is protected within ConnectionHolder unfortunately
        Connection con = null;
        try {
            con = holder.getConnection();
        } catch (IllegalArgumentException ignored) {
        }
        return con;
    }
}
