package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Вызван метод getAll() в UserController");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable int userId) {
        log.info("Вызван метод getById() в UserController");
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Вызван метод save() в UserController");
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto editUser(@RequestBody UserDto user, @PathVariable int userId) {
        log.info("Вызван метод edit() в UserController");
        return userService.edit(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable int userId) {
        log.info("Вызван метод delete() в UserController");
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
