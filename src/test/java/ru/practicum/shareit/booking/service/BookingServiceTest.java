package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.MessageFailedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebMvcTest(BookingService.class)
@AutoConfigureMockMvc
class BookingServiceTest {
    BookingService bookingService;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    Item item;
    User booker;
    User owner;
    Booking booking;
    BookingDto bookingDto;
    BookingDtoIn bookingDtoIn;

    @BeforeEach
    void init() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        booker = new User(1, "booker", "us@mail");
        owner = new User(2, "owner", "ow@email");
        item = new Item(1, "item", "descrItem", true, owner.getId(), null);
        booking = new Booking(1, booker, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item, Status.WAITING);
        bookingDto = new BookingDto(1, item, booking.getStart(), booking.getEnd(), booker, booking.getStatus());
        bookingDtoIn = new BookingDtoIn(bookingDto.getItem().getId(), bookingDto.getStart(), bookingDto.getEnd());
    }

    @Test
    void getById() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        BookingDto res = bookingService.getById(booking.getId(), owner.getId());

        assertNotNull(res);
        assertEquals(booking.getId(), res.getId());
        assertEquals(booking.getBooker(), res.getBooker());
    }

    @Test
    void getByIdNotFound() {
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getById(booking.getId(), owner.getId()));
    }

    @Test
    void confirmation() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto res = bookingService.confirmation(booking.getId(), owner.getId(), true);

        assertNotNull(res);
        assertEquals(booking.getId(), res.getId());
        assertEquals(booking.getItem().getId(), res.getItem().getId());
    }

    @Test
    void save() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingDto res = bookingService.save(booker.getId(), bookingDtoIn);

        assertNotNull(res);
        assertEquals(booking.getId(), res.getId());
        assertEquals(booking.getItem().getId(), res.getItem().getId());
    }

    @Test
    void saveValidateException() {
        BookingDtoIn bookingDtoIn1 = new BookingDtoIn(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(2));
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        assertThrows(ValidateException.class, () -> bookingService.save(booker.getId(), bookingDtoIn1));
    }

    @Test
    void getAllByBookerId() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> res = bookingService.getAllByBookerId(booker.getId(), "ALL", 0, 3);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void getAllByBookerIdStateREJECTED() {
        booking.setStatus(Status.REJECTED);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> res = bookingService.getAllByBookerId(booker.getId(), "REJECTED", 0, 3);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void getAllByBookerIdStatePAST() {
        Booking lastBooking = new Booking(2, booker, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusHours(1), item, Status.APPROVED);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(lastBooking));

        List<BookingDto> res = bookingService.getAllByBookerId(booker.getId(), "PAST", 0, 3);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void getAllByBookerIdStateFUTURE() {
        Booking nextBooking = new Booking(3, booker, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5), item, Status.APPROVED);
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(nextBooking));

        List<BookingDto> res = bookingService.getAllByBookerId(booker.getId(), "FUTURE", 0, 3);

        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void getAllByBookerValidState() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        assertThrows(MessageFailedException.class, () -> bookingService.getAllByBookerId(booker.getId(),
                "ABC", 0, 2));
    }

    @Test
    void getAllByOwnerId() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByOwnerId(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> res = bookingService.getAllByOwnerId(owner.getId(), "WAITING", 0, 3);

        assertNotNull(res);
        assertEquals(1, res.size());
    }
}