package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(long itemId, Item item);

    List<Item> getAllItemsByOwnerId(long ownerId);

    Item getItemById(long itemId);

    List<Item> searchItems(String text);
}
