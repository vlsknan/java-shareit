package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(int userId, String description);
    List<ItemRequestDto> getAll(int userId);
    List<ItemRequestDto> getAllOtherUser(int userId, int from, int size);
    ItemRequestDto getById(int userId, int requestId);
}
