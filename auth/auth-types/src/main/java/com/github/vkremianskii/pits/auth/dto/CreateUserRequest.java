package com.github.vkremianskii.pits.auth.dto;

import com.github.vkremianskii.pits.auth.model.Username;

public record CreateUserRequest(Username username, String password) {

}