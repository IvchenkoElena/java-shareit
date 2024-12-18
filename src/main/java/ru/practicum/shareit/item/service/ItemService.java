package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, NewItemRequest newItemRequest);

    ItemDto updateItem(long ownerId, long itemId, UpdateItemRequest updateItemRequest);

    List<AdvancedItemDto> getAllItems(long ownerId);

    AdvancedItemDto getItemById(long itemId);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(long userId, long itemId, NewCommentRequest newCommentRequest);
}
