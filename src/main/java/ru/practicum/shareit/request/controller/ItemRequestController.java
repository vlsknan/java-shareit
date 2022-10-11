package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(X_SHARER_USER_ID) int userId, @RequestBody @Valid String description) {
        log.info("Вызван метод create() в ItemRequestController");
        return itemRequestService.create(userId, description);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(X_SHARER_USER_ID) int userId) {
        log.info("Вызван метод getAllByUser() в ItemRequestController");
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOtherUser(@RequestHeader(X_SHARER_USER_ID) int userId,
                                            @RequestParam int from, @RequestParam int size) {
        log.info("Вызван метод getAllOtherUser() в ItemRequestController");
        return itemRequestService.getAllOtherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int requestId) {
        log.info("Вызван метод getById() в ItemRequestController");
        return itemRequestService.getById(userId, requestId);
    }
}
