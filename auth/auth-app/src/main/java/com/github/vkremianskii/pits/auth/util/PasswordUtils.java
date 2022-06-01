package com.github.vkremianskii.pits.auth.util;

import com.github.vkremianskii.pits.core.model.Hash;
import com.github.vkremianskii.pits.core.web.error.InternalServerError;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static com.github.vkremianskii.pits.core.model.Hash.hash;

public class PasswordUtils {

    private static final int HASH_NUM_ITERATIONS = 1_000;
    private static final int HASH_KEY_LENGTH = 128;

    private PasswordUtils() {
    }

    public static Hash hashPassword(char[] password, byte[] salt) {
        try {
            final var keySpec = new PBEKeySpec(password, salt, HASH_NUM_ITERATIONS, HASH_KEY_LENGTH);
            final var keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final var encoded = keyFactory.generateSecret(keySpec).getEncoded();
            return hash(Base64.getEncoder().encodeToString(encoded));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalServerError(e);
        }
    }
}
