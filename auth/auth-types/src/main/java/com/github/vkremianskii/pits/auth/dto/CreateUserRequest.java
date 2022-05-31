package com.github.vkremianskii.pits.auth.dto;

import com.github.vkremianskii.pits.auth.model.Scope;
import com.github.vkremianskii.pits.auth.model.Username;

import java.util.Set;

public record CreateUserRequest(Username username,
                                String password,
                                Set<Scope> scopes) {

}
