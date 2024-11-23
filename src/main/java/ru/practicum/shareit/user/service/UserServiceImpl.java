package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest request) {
        log.info("Вызван сервисный метод сохранения пользователя");
        long currentId = userRepository.getCurrentId();
        User newUser = UserMapper.mapToUser(currentId, request);
        userRepository.createUser(newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(long userId, UpdateUserRequest request) {
        log.info("Вызван сервисный метод обновления пользователя");
        User updatedUser = userRepository.getUserById(userId);
        UserMapper.updateUserFields(updatedUser, request);
        userRepository.updateUser(userId, updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Вызван сервисный метод вывода списка пользователей");
        return userRepository.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Вызван сервисный метод вывода пользователя с ID {}", userId);
        User user = userRepository.getUserById(userId);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUserById(long userId) {
        log.info("Вызван сервисный метод удаления пользователя с ID {}", userId);
        userRepository.deleteUserById(userId);
    }
}
