package com.github.vkremianskii.pits.auth.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vkremianskii.pits.auth.model.PasswordHash;
import com.github.vkremianskii.pits.auth.model.Scope;
import com.github.vkremianskii.pits.auth.model.User;
import com.github.vkremianskii.pits.auth.model.UserId;
import com.github.vkremianskii.pits.auth.model.Username;
import com.github.vkremianskii.pits.core.data.TransactionalJooq;
import com.github.vkremianskii.pits.core.web.error.InternalServerError;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Table;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.github.vkremianskii.pits.auth.model.PasswordHash.passwordHash;
import static com.github.vkremianskii.pits.auth.model.UserId.userId;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class UserRepository {

    private static final Table<?> TABLE = table("user");
    private static final Field<UUID> FIELD_ID = field("id", UUID.class);
    private static final Field<String> FIELD_USERNAME = field("username", String.class);
    private static final Field<String> FIELD_PASSWORD = field("password", String.class);
    private static final Field<JSONB> FIELD_SCOPES = field("scopes", JSONB.class);

    private final TransactionalJooq transactionalJooq;
    private final ObjectMapper objectMapper;

    public UserRepository(TransactionalJooq transactionalJooq,
                          ObjectMapper objectMapper) {
        this.transactionalJooq = requireNonNull(transactionalJooq);
        this.objectMapper = requireNonNull(objectMapper);
    }

    public Mono<Void> createUser(UserId userId,
                                 Username username,
                                 PasswordHash password) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.insertInto(TABLE)
                .columns(FIELD_ID, FIELD_USERNAME, FIELD_PASSWORD)
                .values(userId.value, username.value, password.value))
            .then());
    }

    public Mono<Optional<User>> getUserByName(Username username) {
        return transactionalJooq.inTransactionalContext(ctx -> Mono.from(ctx.selectFrom(TABLE)
                .where(FIELD_USERNAME.eq(username.value)))
            .map(r -> r.map(this::userFromRecord))
            .map(Optional::of)
            .switchIfEmpty(Mono.just(Optional.empty())));
    }

    private User userFromRecord(org.jooq.Record record) {
        final var id = userId(record.get(FIELD_ID));
        final var username = username(record.get(FIELD_USERNAME));
        final var password = passwordHash(record.get(FIELD_PASSWORD));
        final var scopes = record.get(FIELD_SCOPES);

        return new User(
            id,
            username,
            password,
            parseScopes(scopes));
    }

    private Set<Scope> parseScopes(JSONB jsonb) {
        try {
            return objectMapper.readValue(jsonb.data(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new InternalServerError(e);
        }
    }
}
