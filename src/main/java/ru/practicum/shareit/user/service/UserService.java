package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest request);

    UserDto updateUser(long userId, UpdateUserRequest request);

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    void deleteUserById(long userId);
}
