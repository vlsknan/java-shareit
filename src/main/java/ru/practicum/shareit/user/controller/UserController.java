package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable int userId) {
        return userService.getById(userId);
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody UserDto user) {
        return userService.save(user);
    }

    @PatchMapping("/{userId}")
    public User editUser(@RequestBody UserDto user, @PathVariable int userId) {
        return userService.edit(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable int userId) {
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
