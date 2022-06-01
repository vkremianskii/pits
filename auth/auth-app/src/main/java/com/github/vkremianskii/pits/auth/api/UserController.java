package com.github.vkremianskii.pits.auth.api;

import com.github.vkremianskii.pits.auth.dto.AuthenticateRequest;
import com.github.vkremianskii.pits.auth.dto.AuthenticateResponse;
import com.github.vkremianskii.pits.auth.dto.CreateUserRequest;
import com.github.vkremianskii.pits.auth.dto.CreateUserResponse;
import com.github.vkremianskii.pits.auth.logic.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin')")
    public Mono<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(
                request.username(),
                request.password().toCharArray(),
                request.scopes())
            .map(CreateUserResponse::new);
    }

    @PostMapping("/auth")
    public Mono<AuthenticateResponse> authenticateUser(@RequestBody AuthenticateRequest request) {
        return userService.authenticateUser(request.username(), request.password().toCharArray())
            .map(auth -> new AuthenticateResponse(auth.first(), auth.second()));
    }
}
