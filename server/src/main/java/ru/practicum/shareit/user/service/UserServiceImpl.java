package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest request) {
        log.info("Вызван сервисный метод сохранения пользователя");
        User newUser = UserMapper.mapToUser(request);
        validateUserEmail(newUser);
        newUser = userRepository.save(newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UpdateUserRequest request) {
        log.info("Вызван сервисный метод обновления пользователя");
        User oldUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User newUser = UserMapper.updateUserFields(oldUser, request);
        validateUserEmail(newUser);
        newUser = userRepository.save(newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Вызван сервисный метод вывода списка пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Вызван сервисный метод вывода пользователя с ID {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        log.info("Вызван сервисный метод удаления пользователя с ID {}", userId);
        userRepository.deleteById(userId);
    }

    private void validateUserEmail(User user) {
        long id = user.getId();
        String email = user.getEmail();
        if (userRepository.findAll().stream()
                .filter(u -> u.getId() != id)
                .map(User::getEmail)
                .toList()
                .contains(email)) {
            String message = "Такой адрес электронной почты уже используется";
            log.error(message);
            throw new ConflictException(message);
        }
    }
}
