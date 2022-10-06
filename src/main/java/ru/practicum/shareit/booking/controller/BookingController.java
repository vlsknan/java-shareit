package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
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
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Вызван метод getAllByBookerId() в BookingController");
        return bookingService.getAllByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByUser(@RequestHeader(X_SHARER_USER_ID) int ownerId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Вызван метод getBookingByUser() в BookingController");
        return bookingService.getAllByOwnerId(ownerId, state);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(X_SHARER_USER_ID) int userId,
                             @Valid @RequestBody BookingDtoIn bookingDto) {
        log.info("Вызван метод save() в BookingController");
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto confirmation(@RequestHeader(X_SHARER_USER_ID) int userId,
                                   @PathVariable int bookingId,
                                   @RequestParam boolean approved) {
        log.info("Вызван метод confirmation() в BookingController");
        return bookingService.confirmation(bookingId, userId, approved);
    }

}
