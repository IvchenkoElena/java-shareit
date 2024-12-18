package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody NewItemRequest newItemRequest) {
        log.info("Вызван эндпоинт создания вещи");
        return itemService.createItem(userId, newItemRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable("itemId") long itemId,
                              @RequestBody UpdateItemRequest updateItemRequest) {
        log.info("Вызван эндпоинт обновления вещи с ID {}", itemId);
        return itemService.updateItem(userId, itemId, updateItemRequest);
    }

    @GetMapping
    public List<AdvancedItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вызван эндпоинт получения списка вещей");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public AdvancedItemDto getItemById(@PathVariable("itemId") long itemId) {
        log.info("Вызван эндпоинт получения вещи с ID {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        log.info("Вызван эндпоинт поиска вещей");
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable("itemId") long itemId,
                                    @Valid @RequestBody NewCommentRequest newCommentRequest) {
        log.info("Вызван эндпоинт создания комментария");
        return itemService.createComment(userId, itemId, newCommentRequest);
    }
}