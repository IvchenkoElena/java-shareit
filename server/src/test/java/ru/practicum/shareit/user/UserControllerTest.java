package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private NewUserRequest userInputDto;
    private UserDto userOutputDto;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        userInputDto = new NewUserRequest();
        userInputDto.setName("Test User");
        userInputDto.setEmail("test@example.com");

        userOutputDto = new UserDto();
        userOutputDto.setId(userId);
        userOutputDto.setName(userInputDto.getName());
        userOutputDto.setEmail(userInputDto.getEmail());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userOutputDto));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(userOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].name", is(userOutputDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userOutputDto.getEmail())));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userOutputDto);

        mockMvc.perform(get("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userOutputDto.getName())))
                .andExpect(jsonPath("$.email", is(userOutputDto.getEmail())));
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any(NewUserRequest.class))).thenReturn(userOutputDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userOutputDto.getName())))
                .andExpect(jsonPath("$.email", is(userOutputDto.getEmail())));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(userOutputDto);

        mockMvc.perform(patch("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(userOutputDto.getName())))
                .andExpect(jsonPath("$.email", is(userOutputDto.getEmail())));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}