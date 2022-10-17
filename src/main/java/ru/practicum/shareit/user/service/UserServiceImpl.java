package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAll() {
        log.info("Получен список всех пользователей (getAll())");
        return userRepository.findAll().stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());
    }

    public UserDto getById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", id)));
        log.info("Получен пользователь с id = {}", id);
        return UserMapper.toUser(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUserDto(userDto));
        log.info("Пользователь с id = {} создан", user.getId());
        return UserMapper.toUser(user);
    }

    @Transactional
    public UserDto edit(UserDto userDto, int id) {
        User oldUser = UserMapper.toUserDto(getById(id));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            oldUser.setEmail(userDto.getEmail());
        }
        log.info("Данные пользователя с id = {} обновлены", oldUser.getId());
        User user = userRepository.save(oldUser);
        return UserMapper.toUser(user);
    }

    @Transactional
    public void delete(int id) {
        getById(id);
        userRepository.deleteById(id);
        log.info("Пользователь с id = {} удален", id);
    }
}
