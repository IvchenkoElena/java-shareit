package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemCutDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    private final String urlTemplate = "/requests";
    private final String headerUserId = "X-Sharer-User-Id";


    @SneakyThrows
    @Test
    void createItemRequest() {
        NewItemRequestRequest requestDto = new NewItemRequestRequest("description");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", new UserDto(2L, "requestor name", "requestor email"),
                LocalDateTime.of(2022, 7, 3, 0, 0, 0));

        when(requestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mvc.perform(post(urlTemplate)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserId, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @SneakyThrows
    @Test
    void getItemRequestByRequestId() {
        NewItemRequestRequest requestDto = new NewItemRequestRequest("description");
        ItemRequestWithAnswersDto itemRequestWithAnswersDto = new ItemRequestWithAnswersDto(1L, "description", new UserDto(2L, "requestor name", "requestor email"),
                LocalDateTime.of(2022, 7, 3, 0, 0, 0), List.of(new ItemCutDto()));

        when(requestService.getItemRequestByRequestId(anyLong(), anyLong())).thenReturn(itemRequestWithAnswersDto);

        mvc.perform(get(urlTemplate + "/" + itemRequestWithAnswersDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserId, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @SneakyThrows
    @Test
    void getAllItemRequests() {
        NewItemRequestRequest requestDto = new NewItemRequestRequest("description");
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "description", new UserDto(2L, "requestor name", "requestor email"),
                LocalDateTime.of(2022, 7, 3, 0, 0, 0));
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(1L, "description", new UserDto(2L, "requestor name", "requestor email"),
                LocalDateTime.of(2022, 7, 3, 0, 0, 0));

        List<ItemRequestDto> newRequests = List.of(itemRequestDto1, itemRequestDto2);

        when(requestService.getAllItemRequests(anyLong())).thenReturn(newRequests);

        mvc.perform(get(urlTemplate + "/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(is(newRequests.getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[1].id").value(is(newRequests.getLast().getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(newRequests)));
    }
}