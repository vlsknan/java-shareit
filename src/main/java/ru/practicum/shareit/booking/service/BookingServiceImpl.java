package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto getById(int bookingId, int ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %s не найден", bookingId)));
        Item item = itemRepository.findById(booking.getItem().getId()).get();
        if (booking.getBooker().getId() == ownerId || item.getOwnerId() == ownerId) {
            log.info("Найден запрос с id = {} (getById())", booking.getId());
            return BookingMapper.toBookingDto(booking);
        }
        throw new NotFoundException("Получить данные о бронировании может автор бронирования или владелец вещи");
    }

    @Override
    @Transactional
    public BookingDto save(BookingDtoIn bookingDtoIn, int ownerId) {
        User booker = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", ownerId)));
        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена", bookingDtoIn.getItemId())));
        BookingDto bookingDto = BookingMapper.toBookingDto(bookingDtoIn, item);
        validateBooking(bookingDto);
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
        if (booker.getId() == item.getOwnerId()) {
            throw new NotFoundException(String.format("Вы являетесь владельцем вещи с id = %s", item.getId()));
        }
        if (item.getAvailable()) {
            Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
            booking.setStatus(Status.WAITING);
            log.info("Запрос с id = {} сохранен (save())", booking.getId());
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        throw new ValidateException(String.format("Вещь с id = %s недоступна для аренды", bookingDto.getItem().getId()));
    }

    @Override
    @Transactional
    public BookingDto confirmation(int bookingId, int ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %s не найден", bookingId)));
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", ownerId)));
        itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена", booking.getItem().getId())));

        if (booking.getItem().getOwnerId() == ownerId) {
            if (booking.getStatus().equals(Status.WAITING)) {
                booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
            } else {
                throw new ValidateException("Изменение статуса бронирования недоступно");
            }
            log.info("Статус бронированиня у запроса с id = {} изменен на {} (confirmation())", booking.getId(), booking.getStatus());
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        throw new NotFoundException("Редактировать статус бронирования может только владелец вещи");
    }

    @Override
    public List<BookingDto> getAllByBookerId(int bookerId, BookingState state) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", bookerId)));
        Set<Booking> bookings = new HashSet<>(bookingRepository.findAllByBookerId(bookerId));
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирований не найдено.");
        } else {
            log.info("Получены все бронирования пользователя с id = {} (getAllByBookerId())", bookerId);
            return filterByState(bookings, state);
        }
    }

    @Override
    public List<BookingDto> getAllByOwnerId(int ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", ownerId)));
        Set<Booking> bookings = new HashSet<>(bookingRepository.findAllByOwnerId(ownerId));
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирований не найдено.");
        } else {
            log.info("Получены все бронирования пользователя с id = {} (getAllByOwnerId())", ownerId);
            return filterByState(bookings, state);
        }
    }

    private List<BookingDto> filterByState(Set<Booking> bookings, BookingState state) {
        List<BookingDto> bookingList = null;
        switch (state) {
            case ALL:
                bookingList = bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingList = bookings.stream()
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingList = bookings.stream()
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingList = bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingList = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookingList = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                                booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
                break;
        }
        return bookingList;
    }

    private void validateBooking(BookingDto bookingDto) {
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getStart() == null) {
            throw new ValidateException(String.format("Дата начала брони не указана или находится в прошлом"));
        } else if (bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getEnd() == null) {
            throw new ValidateException(String.format("Дата окончания брони не указана или находится в прошлом"));
        } else if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidateException(String.format("Дата окончания брони раньше даты начала"));
        }
    }
}
