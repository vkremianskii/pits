package com.github.vkremianskii.pits.auth.logic;

import com.github.vkremianskii.pits.auth.data.UserRepository;
import com.github.vkremianskii.pits.auth.model.Scope;
import com.github.vkremianskii.pits.auth.model.UserId;
import com.github.vkremianskii.pits.auth.model.Username;
import com.github.vkremianskii.pits.core.Tuple2;
import com.github.vkremianskii.pits.core.model.Hash;
import com.github.vkremianskii.pits.core.web.error.UnauthorizedError;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.UserId.userId;
import static com.github.vkremianskii.pits.auth.util.PasswordUtils.hashPassword;
import static com.github.vkremianskii.pits.core.Tuple2.tuple2;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = requireNonNull(userRepository);
    }

    public Mono<UserId> createUser(Username username, char[] password, Set<Scope> scopes) {
        final var userId = userId(randomUUID());
        final var hash = hashUserPassword(userId, password);

        return userRepository.createUser(userId, username, hash, scopes)
            .thenReturn(userId);
    }

    public Mono<Tuple2<UserId, Set<Scope>>> authenticateUser(Username username, char[] password) {
        return userRepository.getUserByName(username)
            .flatMap(maybeUser -> maybeUser
                .map(user -> {
                    final var hash = hashUserPassword(user.userId(), password);
                    if (hash.equals(user.password())) {
                        return Mono.just(tuple2(user.userId(), user.scopes()));
                    } else {
                        return Mono.<Tuple2<UserId, Set<Scope>>>error(new UnauthorizedError());
                    }
                })
                .orElse(Mono.error(new UnauthorizedError())));
    }

    private static Hash hashUserPassword(UserId userId, char[] password) {
        final var salt = userId.value.toString().getBytes();
        return hashPassword(password, salt);
    }
}
