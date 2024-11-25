package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserInMemoryRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    @Override
    public User createUser(User newUser) {
        newUser.setId(currentId);
        users.put(currentId, newUser);
        log.info("Новый пользователь с ID {} добавлен в репозиторий", currentId);
        currentId++;
        return newUser;
    }

    @Override
    public User updateUser(long userId, User updatedUser) {
        users.remove(userId);
        users.put(userId, updatedUser);
        log.info("Пользователь с ID {} обновлен в репозитории", currentId);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Выведен список пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long userId) {
        if (!users.containsKey(userId)) {
            String message = "Пользователь с ID " + userId + " не обнаружен";
            log.error(message);
            throw new NotFoundException(message);
        }
        log.info("Найден пользователь с ID {}", userId);
        return users.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        if (!users.containsKey(userId)) {
            String message = "Пользователь с ID " + userId + " для удаления не обнаружен";
            log.error(message);
            throw new NotFoundException(message);
        }
        users.remove(userId);
        log.info("Удален пользователь с ID {}", userId);
    }
}
