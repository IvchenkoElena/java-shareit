package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_whenUserValid() {
        long userId = 0L;
        NewUserRequest newUser = new NewUserRequest("name", "email");
        UserDto userDto = new UserDto(userId, "name", "email");
        User userToSave = new User(userId, "name", "email");
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUser = userService.createUser(newUser);

        assertEquals(userDto, actualUser);
        verify(userRepository).save(userToSave);
    }

    @Test
    void createUser_whenUserEmailNotValid() {
        long userId = 5L;
        NewUserRequest newUser = new NewUserRequest("name", "email");
        UserDto userDto = new UserDto(userId, "name", "email");
        User userToSave = new User(userId, "name", "email");
        when(userRepository.findAll()).thenReturn(List.of(new User(10L, "Some name", "email")));

        ConflictException thrown = assertThrows(ConflictException.class, () -> userService.createUser(newUser));
        assertEquals("Такой адрес электронной почты уже используется", thrown.getMessage());
        verify(userRepository, never()).save(userToSave);
    }

    @Test
    void updateUser_whenOldUserExist() {
        long userId = 5L;
        User oldUser = new User(userId, "name", "email");
        UpdateUserRequest newUser = new UpdateUserRequest("name2", "email2");
        User userToSave = new User(userId, "name2", "email2");
        UserDto userDto = new UserDto(userId, "name2", "email2");
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUser = userService.updateUser(userId, newUser);

        assertEquals(userDto, actualUser);
        verify(userRepository).save(userToSave);
    }

    @Test
    void updateUser_whenOldUserNotExist() {
        long userId = 5L;
        UpdateUserRequest newUser = new UpdateUserRequest("name2", "email2");
        User userToSave = new User(userId, "name2", "email2");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, newUser));

        assertEquals("Пользователь с ID " + userId + " не найден", thrown.getMessage());
        verify(userRepository, never()).save(userToSave);
    }

    @Test
    void updateUser_whenUserEmailExist() {
        long userId = 5L;
        User oldUser = new User(userId, "name", "email");
        UpdateUserRequest newUser = new UpdateUserRequest("name2", "email");
        User userToSave = new User(userId, "name2", "email");
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.findAll()).thenReturn(List.of(new User(10L, "Some name", "email")));

        ConflictException thrown = assertThrows(ConflictException.class,
                () -> userService.updateUser(userId, newUser));
        assertEquals("Такой адрес электронной почты уже используется", thrown.getMessage());
        verify(userRepository, never()).save(userToSave);
    }

    @Test
    void getAllUsers_PositiveCase() {
        User user1 = new User(5L, "name", "email");
        User user2 = new User(7L, "name2", "email2");
        List<User> usersListToSave = List.of(user1, user2);
        UserDto dto1 = new UserDto(5L, "name", "email");
        UserDto dto2 = new UserDto(7L, "name2", "email2");
        List<UserDto> dtoList = List.of(dto1, dto2);
        when(userRepository.findAll()).thenReturn(usersListToSave);

        List<UserDto> actualDtoList = userService.getAllUsers();

        assertEquals(dtoList, actualDtoList);
    }

    @Test
    void getUserById_whenUserFound() {
        long userId = 0L;
        User expectedUser = new User(userId, "name", "email");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userService.getUserById(userId);

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void getUserById_whenUserNotFound() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }
}