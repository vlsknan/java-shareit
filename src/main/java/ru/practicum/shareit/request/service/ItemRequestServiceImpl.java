package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(int userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = {} не найден", userId)));
        if (itemRequestDto.getDescription() != null) {
            ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester, LocalDateTime.now());
            return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
        }
        throw new ValidateException("Описание запроса не может быть null");
    }

    @Override
    public List<ItemRequestDtoOut> getAll(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = {} не найден", userId)));
        return itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(r -> ItemRequestMapper.toItemRequestDtoOut(r, getItems(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> getAllOtherUser(int userId, int from, int size) {
        int page = from / size;
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = {} не найден", userId)));
        return itemRequestRepository.findAll(PageRequest.of(page, size)).stream()
                .filter(itemRequest -> !Objects.equals(itemRequest.getRequester().getId(), requester.getId()))
                .map(r -> ItemRequestMapper.toItemRequestDtoOut(r, getItems(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOut getById(int userId, int requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = {} не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = {} пользователя " +
                        "с id = {} не найден", requestId, userId)));
        return ItemRequestMapper.toItemRequestDtoOut(itemRequest, getItems(requestId));
    }

    private List<ItemDto> getItems(int id) {
        return itemRepository.findItemByItemRequestId(id).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
