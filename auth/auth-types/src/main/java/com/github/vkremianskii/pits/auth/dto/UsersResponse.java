package com.github.vkremianskii.pits.auth.dto;

import com.github.vkremianskii.pits.auth.model.User;

import java.util.List;

public record UsersResponse(List<User> users) {

}
