package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDtoInfo> getAll(int ownerId, int from, int size) {
        userRepository.findById(ownerId);
        log.info("Получены все вещи пользователя c id = {} (getAll())", ownerId);
        return itemRepository.findAllByOwnerId(ownerId, pagination(from, size)).stream()
                .map(i -> toItemDtoInfo(i, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoInfo getById(int id, int ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена", id)));
        log.info("Найдена вещь с id = {} (getById())", id);
        return toItemDtoInfo(item, ownerId);
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id = %s не имеет бронирований", userId)));
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Запрос на вещь с id = %s не найден",
                            itemDto.getRequestId()))));
        }
        item.setOwnerId(userId);
        log.info("Вещь с id = {} сохранена (create())", item.getId());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto edit(ItemDto itemDto, int userId, int itemId) {
        userRepository.findById(userId);
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена", itemId)));
        if (oldItem.getOwnerId() == userId) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                oldItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                oldItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }
            log.info("Данные о вещи с id = {} обновлены (edit())", oldItem.getId());
            return ItemMapper.toItemDto(itemRepository.save(oldItem));
        }
        throw new NotFoundException("Редактировать информацию о вещи может только ее владелец");
    }

    @Override
    public void delete(int id) {
        itemRepository.deleteById(id);
        log.info("Вещь с id = {} удалена", id);
    }

    @Override
    public List<ItemDto> search(String text, int userId, int from, int size) {
        List<ItemDto> listItem = new ArrayList<>();
        if (text.length() != 0) {
            listItem = itemRepository.search(text, pagination(from, size)).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        log.info("Поиск с параметром text = {}", text);
        return listItem;
    }

    @Override
    @Transactional
    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена", itemId)));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        Booking booking = bookingRepository.findAllByBookerId(userId).stream()
                .filter(b -> b.getItem().getId() == itemId)
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new ValidateException(String.format("Пользователь c id = %s не имеет бронирований", userId)));

        if (booking.getBooker().getId() == userId) {
            Comment comment = CommentMapper.toComment(commentDto, item, author, LocalDateTime.now());
            log.info("Комментарий к вещи с id = {} пользователем с id = {} добавлен", itemId, userId);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        }
        throw new ValidateException("Оставлять отзыв может только тот пользователь, который брал эту вещь в аренду");
    }

    private ItemDtoInfo toItemDtoInfo(Item item, int ownerId) {
        Booking lastBooking = bookingRepository.findLastBooking(item.getId(), ownerId)
                .orElse(null);
        Booking nextBooking = bookingRepository.findNextBooking(item.getId(), ownerId)
                .orElse(null);
        List<CommentDto> commentDtos = commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemDtoInfo itemDtoInfo = ItemMapper.toItemDtoInfo(item);
        if (lastBooking != null) {
            itemDtoInfo.setLastBooking(BookingMapper.toBookingDtoForItem(lastBooking));
        }
        if (nextBooking != null) {
            itemDtoInfo.setNextBooking(BookingMapper.toBookingDtoForItem(nextBooking));
        }
        itemDtoInfo.setComments(commentDtos);
        return itemDtoInfo;
    }

    private PageRequest pagination(int from, int size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return pageRequest;
    }
}
