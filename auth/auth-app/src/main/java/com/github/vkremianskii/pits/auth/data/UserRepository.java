package com.github.vkremianskii.pits.auth.data;

import com.github.vkremianskii.pits.auth.User;
import com.github.vkremianskii.pits.auth.UserId;
import com.github.vkremianskii.pits.core.data.TransactionalJooq;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class UserRepository {

    private static final Table<?> TABLE = table("user");
    private static final Field<UUID> FIELD_ID = field("id", UUID.class);

    private final TransactionalJooq transactionalJooq;

    public UserRepository(TransactionalJooq transactionalJooq) {
        this.transactionalJooq = requireNonNull(transactionalJooq);
    }

    public Mono<List<User>> getUsers() {
        return transactionalJooq.inTransactionalContext(ctx -> Flux.from(ctx.selectFrom(TABLE))
            .map(r -> r.map(UserRepository::userFromRecord))
            .collectList());
    }

    public Mono<Void> createUser() {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.insertInto(TABLE)
            .columns()
            .values())
            .then());
    }

    private static User userFromRecord(org.jooq.Record record) {
        final var id = UserId.userId(record.get(FIELD_ID));
        return new User(id);
    }
}
