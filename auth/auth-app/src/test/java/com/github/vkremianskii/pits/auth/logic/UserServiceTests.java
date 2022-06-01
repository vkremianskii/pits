package com.github.vkremianskii.pits.auth.logic;

import com.github.vkremianskii.pits.auth.data.UserRepository;
import com.github.vkremianskii.pits.auth.model.User;
import com.github.vkremianskii.pits.auth.model.UserId;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.core.model.Hash.hash;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTests {

    UserRepository userRepository = mock(UserRepository.class);
    UserService sut = new UserService(userRepository);

    @Test
    void should_create_user() {
        // given
        when(userRepository.createUser(
            any(),
            eq(username("username")),
            any(),
            eq(Set.of(scope("scope")))))
            .thenReturn(Mono.empty());

        // when
        var userId = sut.createUser(
            username("username"),
            "password".toCharArray(),
            Set.of(scope("scope"))).block();

        // then
        assertThat(userId).isNotNull();
    }

    @Test
    void should_authenticate_user() {
        // given
        var userId = UserId.valueOf("6786e0cb-655f-46b9-ad46-fa8fe398dd3f");
        when(userRepository.getUserByName(username("user")))
            .thenReturn(Mono.just(Optional.of(new User(
                userId,
                username("user"),
                hash("nRJnuQFUraYmqOYCKEwO7g=="),
                Set.of(scope("scope"))))));

        // when
        var scopes = sut.authenticateUser(
            username("user"),
            "user".toCharArray()).block();

        // then
        assertThat(scopes).isEqualTo(Set.of(scope("scope")));
    }
}
