package com.github.vkremianskii.pits.auth.api;

import com.github.vkremianskii.pits.auth.data.UserRepository;
import com.github.vkremianskii.pits.auth.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = requireNonNull(userRepository);
    }

    @GetMapping
    public Mono<List<User>> getUsers() {
        return userRepository.getUsers();
    }

    @PostMapping
    public Mono<Void> createUser() {
        return userRepository.createUser();
    }
}
