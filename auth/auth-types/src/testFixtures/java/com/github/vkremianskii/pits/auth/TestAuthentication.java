package com.github.vkremianskii.pits.auth;

import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.auth.util.AuthenticationUtils.basicAuth;

public class TestAuthentication {

    private TestAuthentication() {
    }

    public static String basicAuthAdmin() {
        return basicAuth(username("admin"), "admin".toCharArray());
    }
}
