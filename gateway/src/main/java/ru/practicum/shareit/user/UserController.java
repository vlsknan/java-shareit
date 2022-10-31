package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {
    UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Вызван метод getAll() в UserController");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable int userId) {
        log.info("Вызван метод getById() в UserController");
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @NotNull @RequestBody UserDto user) {
        log.info("Вызван метод save() в UserController");
        return userClient.create(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> editUser(@Valid @NotNull @RequestBody UserDtoUpdate user, @PathVariable int userId) {
        log.info("Вызван метод edit() в UserController");
        return userClient.edit(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        log.info("Вызван метод delete() в UserController");
        return userClient.delete(userId);
    }
}
