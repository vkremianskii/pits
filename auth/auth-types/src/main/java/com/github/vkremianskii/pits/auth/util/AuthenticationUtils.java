package com.github.vkremianskii.pits.auth.util;

import com.github.vkremianskii.pits.auth.model.Username;

import java.util.Base64;

public class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    public static String basicAuth(Username username, char[] password) {
        var plaintext = username + ":" + String.valueOf(password);
        var base64 = Base64.getEncoder().encodeToString(plaintext.getBytes());
        return "Basic " + base64;
    }
}
