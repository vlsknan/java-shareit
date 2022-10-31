package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    RequestClient requestClient;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    //добавить новый запрос вещи
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) int userId,
                                         @Valid @NotNull @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Вызван метод create() в ItemRequestController");
        return requestClient.create(userId, itemRequestDto);
    }

    //получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(X_SHARER_USER_ID) int userId) {
        log.info("Вызван метод getAllByUser() в ItemRequestController");
        return requestClient.getAllByUser(userId);
    }

    //получить список запросов, созданных другими пользователями(from — индекс первого элемента, начиная с 0,
    // size — количество элементов для отображения)
    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherUser(@RequestHeader(X_SHARER_USER_ID) int userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllOtherUser() в ItemRequestController");
        return requestClient.getAllOtherUser(userId, from, size);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int requestId) {
        log.info("Вызван метод getById() в ItemRequestController");
        return requestClient.getById(userId, requestId);
    }
}
