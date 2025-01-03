package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceIntegrationTest {
    private final EntityManager em;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

    private NewUserRequest userInputDto;
    private UserDto userOutputDto;
    private long userId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
//        Query userQuery = em.createNativeQuery("INSERT INTO Users (id, name, email) " +
//                "VALUES (:id , :name , :email );");
//        userQuery.setParameter("id", "1");
//        userQuery.setParameter("name", "Test User");
//        userQuery.setParameter("email", "newuser@example.com");
//        userQuery.executeUpdate();

        userInputDto = new NewUserRequest();
        userInputDto.setName("Test User");
        userInputDto.setEmail("newuser@example.com");
        userOutputDto = userService.createUser(userInputDto);
        userId = userOutputDto.getId();
    }

    @Test
    void createTest() {
        NewUserRequest newUser = new NewUserRequest("john.doe@mail.com", "John Doe");

        userService.createUser(newUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.name like :nameUser", User.class);
        User user = query.setParameter("nameUser", newUser.getName()).getSingleResult();

        MatcherAssert.assertThat(user.getId(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(user.getName(), Matchers.equalTo(newUser.getName()));
        MatcherAssert.assertThat(user.getEmail(), Matchers.equalTo(newUser.getEmail()));
    }

    @Test
    void getUserTest() {
        UserDto loadUsers = userService.getUserById(userId);

        MatcherAssert.assertThat(loadUsers.getId(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(loadUsers.getName(), Matchers.equalTo("Test User"));
        MatcherAssert.assertThat(loadUsers.getEmail(), Matchers.equalTo("newuser@example.com"));
    }

    @Test
    void testGetAllUsers() {
        userRepository.deleteAll();
        List<NewUserRequest> newUsers = List.of(
                new NewUserRequest("ivan@email", "Ivan Ivanov"),
                new NewUserRequest("petr@email", "Pet Petrov"),
                new NewUserRequest("vasilii@email", "Vasilii Vasiliev"));

        for (NewUserRequest user : newUsers) {
            userService.createUser(user);
        }

        Collection<UserDto> loadUsers = userService.getAllUsers();

        assertThat(loadUsers, hasSize(newUsers.size()));
        for (NewUserRequest user : newUsers) {
            assertThat(loadUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @Test
    void updateUserTest() {
        UpdateUserRequest updUser = new UpdateUserRequest("john.doe1@mail.com", "John Doe");
        UserDto findUser = userService.updateUser(userId, updUser);

        MatcherAssert.assertThat(findUser.getId(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(findUser.getName(), Matchers.equalTo(updUser.getName()));
        MatcherAssert.assertThat(findUser.getEmail(), Matchers.equalTo(updUser.getEmail()));
    }

    @Test
    void deleteUserTest() {
        userService.deleteUserById(userId);

        TypedQuery<User> selectQuery = em.createQuery("Select u from User u where u.name like :nameUser", User.class);
        List<User> users = selectQuery.setParameter("nameUser", "Ivan Ivanov").getResultList();

        MatcherAssert.assertThat(users, CoreMatchers.equalTo(new ArrayList<>()));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        NewUserRequest duplicateUserInputDto = new NewUserRequest();
        duplicateUserInputDto.setName("Duplicate User");
        duplicateUserInputDto.setEmail("newuser@example.com");

        ConflictException thrown = assertThrows(ConflictException.class, () -> userService.createUser(duplicateUserInputDto));
        assertEquals("Такой адрес электронной почты уже используется", thrown.getMessage());
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        NewUserRequest exampleUserInputDto = new NewUserRequest();
        exampleUserInputDto.setName("Example User");
        exampleUserInputDto.setEmail("example@example.com");
        UserDto exampleUserDto = userService.createUser(exampleUserInputDto);
        long exampleUserDtoId = exampleUserDto.getId();

        UpdateUserRequest duplicateUserInputDto = new UpdateUserRequest();
        duplicateUserInputDto.setName("Duplicate User");
        duplicateUserInputDto.setEmail("newuser@example.com");

        ConflictException thrown = assertThrows(ConflictException.class,
                () -> userService.updateUser(exampleUserDtoId, duplicateUserInputDto));
        assertEquals("Такой адрес электронной почты уже используется", thrown.getMessage());
    }
    //долго билась над этим тестом, но он почему-то не работает
    //получилось заставить его работать, только удалив unique constrained в Schema
    //добавление валидации в контроллере gateway ничем мне не помогало в server
}