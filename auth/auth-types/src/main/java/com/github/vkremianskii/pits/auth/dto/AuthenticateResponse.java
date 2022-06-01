package com.github.vkremianskii.pits.auth.dto;

import com.github.vkremianskii.pits.auth.model.Scope;
import com.github.vkremianskii.pits.auth.model.UserId;

import java.util.Set;

public record AuthenticateResponse(UserId userId,
                                   Set<Scope> scopes) {

}
