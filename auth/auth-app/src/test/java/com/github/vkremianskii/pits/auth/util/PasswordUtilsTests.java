package com.github.vkremianskii.pits.auth.util;

import org.junit.jupiter.api.Test;

import static com.github.vkremianskii.pits.core.model.Hash.hash;
import static com.github.vkremianskii.pits.auth.util.PasswordUtils.hashPassword;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordUtilsTests {

    @Test
    void should_hash_password() {
        // given
        var password = "password".toCharArray();
        var salt = "e03e08cb-2104-4ee6-aaf7-7a8e864cacce".getBytes();

        // when
        var hash = hashPassword(password, salt);

        // then
        assertThat(hash).isEqualTo(hash("3ySEyb7u9C+zx5CpIBYB0w=="));
    }
}
