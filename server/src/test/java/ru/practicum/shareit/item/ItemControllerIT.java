package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final String urlTemplate = "/items";
    private final String headerUserId = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void createItem() {
        long userId = 6L;
        NewItemRequest request = new NewItemRequest("item name", "item description", true, 0L);
        ItemDto dto = new ItemDto(0L, "item name", "item description", true, userId, 0L);
        when(itemService.createItem(userId, request)).thenReturn(dto);

        String result = mvc.perform(post(urlTemplate)
                        .contentType("application/json")
                        .header(headerUserId, 6L)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(dto), result);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        ItemDto requestDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(requestDto);

        mvc.perform(patch(urlTemplate + "/" + requestDto.getId())
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @SneakyThrows
    @Test
    void getItems() {
        CommentDto comment = new CommentDto(1L, "text", 1L, 1L, "authorName", LocalDateTime.of(2022, 7, 3, 19, 30, 1));
        AdvancedItemDto requestDto1 = new AdvancedItemDto(1L, "name1", "description1", true, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 4, 19, 30, 1), List.of(comment), 1L, 1L);
        AdvancedItemDto requestDto2 = new AdvancedItemDto(1L, "name2", "description2", true, LocalDateTime.of(2023, 7, 3, 19, 30, 1),
                LocalDateTime.of(2023, 7, 4, 19, 30, 1), List.of(comment), 1L, 1L);

        List<AdvancedItemDto> newRequests = List.of(requestDto1, requestDto2);

        when(itemService.getAllItems(anyLong())).thenReturn(newRequests);

        mvc.perform(get(urlTemplate)
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

    @SneakyThrows
    @Test
    void getItemById() {
        CommentDto comment = new CommentDto(1L, "text", 1L, 1L, "authorName", LocalDateTime.of(2022, 7, 3, 19, 30, 1));
        AdvancedItemDto requestDto = new AdvancedItemDto(1L, "name", "description", true, LocalDateTime.of(2022, 7, 3, 19, 30, 1),
                LocalDateTime.of(2022, 7, 4, 19, 30, 1), List.of(comment), 1L, 1L);

        when(itemService.getItemById(anyLong())).thenReturn(requestDto);

        mvc.perform(get(urlTemplate + "/" + requestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)))
                .andExpect(jsonPath("$.comments").exists())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments[0].id").value(is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text").value(is(comment.getText()), String.class))
                .andExpect(jsonPath("$.comments[0].authorName").value(is(comment.getAuthorName()), String.class));
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentDto comment = new CommentDto(1L, "text", 1L, 1L, "authorName", LocalDateTime.of(2022, 7, 3, 19, 30, 1));

        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(post(urlTemplate + "/" + comment.getId() + "/comment")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(headerUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists())
                .andExpect(content().json(mapper.writeValueAsString(comment)));
    }
}