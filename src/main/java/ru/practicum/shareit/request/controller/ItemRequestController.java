package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    //добавить новый запрос вещи
    @PostMapping
    public ItemRequestDto create(@RequestHeader(X_SHARER_USER_ID) int userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Вызван метод create() в ItemRequestController");
        return itemRequestService.create(userId, itemRequestDto);
    }

    //получить список своих запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestDtoOut> getAllByUser(@RequestHeader(X_SHARER_USER_ID) int userId) {
        log.info("Вызван метод getAllByUser() в ItemRequestController");
        return itemRequestService.getAll(userId);
    }

    //получить список запросов, созданных другими пользователями(from — индекс первого элемента, начиная с 0,
    // size — количество элементов для отображения)
    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllOtherUser(@RequestHeader(X_SHARER_USER_ID) int userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllOtherUser() в ItemRequestController");
        return itemRequestService.getAllOtherUser(userId, from, size);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getById(@RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int requestId) {
        log.info("Вызван метод getById() в ItemRequestController");
        return itemRequestService.getById(userId, requestId);
    }
}
