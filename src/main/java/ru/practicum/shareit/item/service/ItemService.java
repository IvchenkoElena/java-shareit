package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long ownerId, NewItemRequest newItemRequest);

    ItemDto updateItem(long ownerId, long itemId, UpdateItemRequest updateItemRequest);

    List<ItemDto> getAllItems(long ownerId);

    ItemDto getItemById(long ownerId, long itemId);

    List<ItemDto> searchItems(String text);
}
