package com.github.vkremianskii.pits.auth.dto;

import com.github.vkremianskii.pits.auth.model.User;

import java.util.List;

public record GetUsersResponse(List<User> users) {

}
