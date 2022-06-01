package com.github.vkremianskii.pits.auth.model;

import com.github.vkremianskii.pits.core.model.Hash;

import java.util.Set;

public record User(UserId userId,
                   Username username,
                   Hash password,
                   Set<Scope> scopes) {

}
