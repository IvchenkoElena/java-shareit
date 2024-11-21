package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(long userId, User user);

    List<User> getAllUsers();

    User getUserById(long userId);

    void deleteUserById(long userId);
}