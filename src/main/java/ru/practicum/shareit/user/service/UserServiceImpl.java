package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<User> getAll() {
       return userRepository.findAll();
    }

    public UserDto getById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", id)));
        return UserMapper.toUser(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return UserMapper.toUser(userRepository.save(UserMapper.toUserDto(userDto)));
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
        User user = userRepository.save(oldUser);
        return UserMapper.toUser(user);
    }

    @Transactional
    public void delete(int id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
