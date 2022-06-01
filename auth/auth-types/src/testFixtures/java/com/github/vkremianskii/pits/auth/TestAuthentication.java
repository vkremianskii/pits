package com.github.vkremianskii.pits.auth;

import com.github.vkremianskii.pits.auth.model.Username;

import java.util.Base64;

import static com.github.vkremianskii.pits.auth.model.Username.username;

public class TestAuthentication {

    private TestAuthentication() {
    }

    public static String basicAuth(Username username, char[] password) {
        var plaintext = username + ":" + String.valueOf(password);
        var base64 = Base64.getEncoder().encodeToString(plaintext.getBytes());
        return "Basic " + base64;
    }

    public static String basicAuthAdmin() {
        return basicAuth(username("admin"), "admin".toCharArray());
    }
}
