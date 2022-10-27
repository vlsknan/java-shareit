package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(int userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester, LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoOut> getAll(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        return itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(r -> ItemRequestMapper.toItemRequestDtoOut(r, getItems(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> getAllOtherUser(int userId, int from, int size) {
        int page = from / size;
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        return itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(page, size,
                        Sort.by(Sort.Direction.DESC, "created"))).stream()
                .map(r -> ItemRequestMapper.toItemRequestDtoOut(r, getItems(r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOut getById(int userId, int requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %s пользователя " +
                        "с id = %s не найден", requestId, userId)));
        return ItemRequestMapper.toItemRequestDtoOut(itemRequest, getItems(requestId));
    }

    private List<ItemDto> getItems(int id) {
        return itemRepository.findItemByItemRequestId(id).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
