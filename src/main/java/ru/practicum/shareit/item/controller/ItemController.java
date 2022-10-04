package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.Update;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDtoInfo> getAll(@RequestHeader(X_SHARER_USER_ID) int userId) {
        log.info("Вызван метод getAll() в ItemController");
        return itemService.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoInfo getById(@RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int itemId) {
        log.info("Вызван метод getById() в ItemController");
        return itemService.getById(itemId, userId);
    }

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(X_SHARER_USER_ID) int userId) {
        log.info("Вызван метод save() в ItemController");
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int itemId) {
        log.info("Вызван метод edit() в ItemController");
        return itemService.edit(itemDto, userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<HttpStatus> deleteItem(@PathVariable int itemId) {
        log.info("Вызван метод delete() в ItemController");
        itemService.delete(itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text, @RequestHeader(X_SHARER_USER_ID) int userId) {
        log.info("Вызван метод search() в ItemController");
        return itemService.search(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int itemId,
                                 @Valid @RequestBody CommentDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }
}
