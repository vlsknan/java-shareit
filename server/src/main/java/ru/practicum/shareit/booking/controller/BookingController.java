package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("{bookingId}")
    public BookingDto getById(@RequestHeader(X_SHARER_USER_ID) int ownerId,
                              @PathVariable int bookingId) {
        log.info("Вызван метод getById() в BookingController");
        return bookingService.getById(bookingId, ownerId);
    }

    @GetMapping
    public List<BookingDto> getAllByBookerId(@RequestHeader(X_SHARER_USER_ID) int bookerId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllByBookerId() в BookingController");
        return bookingService.getAllByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerId(@RequestHeader(X_SHARER_USER_ID) int ownerId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("Вызван метод getAllByOwnerId() в BookingController");
        return bookingService.getAllByOwnerId(ownerId, state, from, size);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) int userId,
                             @RequestBody BookingDtoIn bookingDto) {
        log.info("Вызван метод save() в BookingController");
        return bookingService.save(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDto confirmation(@RequestHeader(X_SHARER_USER_ID) int userId,
                                   @PathVariable int bookingId,
                                   @RequestParam boolean approved) {
        log.info("Вызван метод confirmation() в BookingController");
        return bookingService.confirmation(bookingId, userId, approved);
    }

}