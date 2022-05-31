package com.github.vkremianskii.pits.auth.model;

import java.util.Set;

public record User(UserId userId,
                   Username username,
                   PasswordHash password,
                   Set<Scope> scopes) {

}
