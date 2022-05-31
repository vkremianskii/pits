package com.github.vkremianskii.pits.auth.dto;

import com.github.vkremianskii.pits.auth.model.Username;

public record AuthenticateRequest(Username username, String password) {

}
