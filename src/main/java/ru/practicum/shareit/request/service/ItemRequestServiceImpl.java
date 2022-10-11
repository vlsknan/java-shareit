package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;


    @Override
    public ItemRequestDto create(int userId, String description) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = {} не найден", userId)));
        ItemRequest itemRequest = ;
        return itemRequestRepository.save();
    }

    @Override
    public List<ItemRequestDto> getAll(int userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllOtherUser(int userId, int from, int size) {
        return null;
    }

    @Override
    public ItemRequestDto getById(int userId, int requestId) {
        return null;
    }
}
