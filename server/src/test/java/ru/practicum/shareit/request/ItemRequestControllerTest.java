package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private NewItemRequestRequest itemRequestInputDto;
    private ItemRequestDto itemRequestOutputDto;
    private ItemRequestWithAnswersDto itemRequestWithAnswersDto;
    private Long userId = 1L;
    private Long requestId = 1L;

    @BeforeEach
    void setUp() {
        itemRequestInputDto = new NewItemRequestRequest();
        itemRequestInputDto.setDescription("Test Request");

        itemRequestOutputDto = new ItemRequestDto();
        itemRequestOutputDto.setId(requestId);
        itemRequestOutputDto.setDescription(itemRequestInputDto.getDescription());

        itemRequestWithAnswersDto = new ItemRequestWithAnswersDto();
        itemRequestWithAnswersDto.setId(requestId);
        itemRequestWithAnswersDto.setDescription(itemRequestInputDto.getDescription());
        itemRequestWithAnswersDto.setRequestor(new UserDto(userId, "name", "descriotion"));
        itemRequestWithAnswersDto.setCreated(LocalDateTime.now());
        itemRequestWithAnswersDto.setItems(new ArrayList<>());
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(NewItemRequestRequest.class))).thenReturn(itemRequestOutputDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestOutputDto.getDescription())));
    }

    @Test
    void testGetItemRequestsByRequestorId() throws Exception {
        when(itemRequestService.getItemRequestsByRequestorId(anyLong())).thenReturn(List.of(itemRequestWithAnswersDto));

        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequestOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestOutputDto.getDescription())));
    }

    @Test
    void getAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong())).thenReturn(List.of(itemRequestOutputDto));

        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequestOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestOutputDto.getDescription())));
    }

    @Test
    void testGetItemRequestByRequestId() throws Exception {
        when(itemRequestService.getItemRequestByRequestId(anyLong(), anyLong())).thenReturn(itemRequestWithAnswersDto);

        mockMvc.perform(get("/requests" + "/" + itemRequestWithAnswersDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestWithAnswersDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestWithAnswersDto.getDescription())));
    }
}