package com.github.vkremianskii.pits.auth.logic;

import com.github.vkremianskii.pits.auth.data.UserRepository;
import com.github.vkremianskii.pits.auth.model.PasswordHash;
import com.github.vkremianskii.pits.auth.model.Scope;
import com.github.vkremianskii.pits.auth.model.UserId;
import com.github.vkremianskii.pits.auth.model.Username;
import com.github.vkremianskii.pits.core.web.error.InternalServerError;
import com.github.vkremianskii.pits.core.web.error.UnauthorizedError;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.PasswordHash.passwordHash;
import static com.github.vkremianskii.pits.auth.model.UserId.userId;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

@Service
public class UserService {

    private static final int HASH_NUM_ITERATIONS = 1;
    private static final int HASH_KEY_LENGTH = 128;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = requireNonNull(userRepository);
    }

    public Mono<UserId> createUser(Username username, char[] password, Set<Scope> scopes) {
        final var userId = userId(randomUUID());
        final var hash = hash(password, userId);

        return userRepository.createUser(userId, username, hash, scopes)
            .thenReturn(userId);
    }

    public Mono<Set<Scope>> authenticate(Username username, char[] password) {
        return userRepository.getUserByName(username)
            .flatMap(maybeUser -> maybeUser
                .map(user -> {
                    final var hash = hash(password, user.userId());
                    if (hash.equals(user.password())) {
                        return Mono.just(user.scopes());
                    } else {
                        return Mono.<Set<Scope>>error(new UnauthorizedError());
                    }
                })
                .orElse(Mono.error(new UnauthorizedError())));
    }

    private static PasswordHash hash(char[] password, UserId userId) {
        final var salt = userId.value.toString().getBytes();
        return hash(password, salt);
    }

    private static PasswordHash hash(char[] password, byte[] salt) {
        try {
            final var keySpec = new PBEKeySpec(password, salt, HASH_NUM_ITERATIONS, HASH_KEY_LENGTH);
            final var keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final var encoded = keyFactory.generateSecret(keySpec).getEncoded();
            return passwordHash(Base64.getEncoder().encodeToString(encoded));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalServerError(e);
        }
    }
}
