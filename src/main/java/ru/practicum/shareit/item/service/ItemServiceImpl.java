package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long ownerId, NewItemRequest newItemRequest) {
        User owner = userRepository.getUserById(ownerId);
        Item item = ItemMapper.mapToItem(owner, newItemRequest);
        item = itemRepository.createItem(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long ownerId, long itemId, UpdateItemRequest request) {
        User owner = userRepository.getUserById(ownerId);
        Item updatedItem = itemRepository.getItemById(itemId);
        if (updatedItem.getOwner().getId() != owner.getId()) {
            String message = "У вещи с ID " + itemId + " другой владелец";
            log.error(message);
            throw new NotFoundException(message);
        }
        ItemMapper.updateItemFields(updatedItem, request);
        updatedItem = itemRepository.updateItem(itemId, updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        return itemRepository.getAllItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
