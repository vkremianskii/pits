package com.github.vkremianskii.pits.auth.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static com.github.vkremianskii.pits.auth.TestUser.randomUserId;
import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.core.model.Hash.hash;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTests {

    @Autowired
    UserRepository sut;

    @Test
    void should_create_and_get_user_by_name() {
        // when
        var userId = randomUserId();
        sut.createUser(
            userId,
            username("username"),
            hash("hash"),
            Set.of(scope("scope"))).block();
        var user = sut.getUserByName(username("username")).block();

        // then
        assertThat(user).hasValueSatisfying(u -> {
            assertThat(u.userId()).isEqualTo(userId);
            assertThat(u.username()).isEqualTo(username("username"));
            assertThat(u.password()).isEqualTo(hash("hash"));
            assertThat(u.scopes()).isEqualTo(Set.of(scope("scope")));
        });
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
