package ru.practicum.shareit.item.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwner(item.getOwner());
        dto.setAvailable(item.getAvailable());
        dto.setRequest(item.getRequest());
        return dto;
    }

    public static Item mapToItem(long currentId, User owner, NewItemRequest newItemRequest) {
        Item item = new Item();
        item.setId(currentId);
        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setOwner(owner);
        item.setAvailable(newItemRequest.getAvailable());
        item.setRequest(newItemRequest.getRequest());
        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }
}
