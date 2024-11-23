package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User newUser);

    User updateUser(long userId, User user);

    List<User> getAllUsers();

    User getUserById(long userId);

    void deleteUserById(long userId);

    long getCurrentId();
}
