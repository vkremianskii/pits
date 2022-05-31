package com.github.vkremianskii.pits.auth.dto;

import com.github.vkremianskii.pits.auth.model.Scope;

import java.util.Set;

public record AuthenticateResponse(Set<Scope> scopes) {

}
