package ru.practicum.shareit.item.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item mapToItem(User owner, ItemRequest itemRequest, NewItemRequest newItemRequest) {
        Item item = new Item();
        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setOwner(owner);
        item.setAvailable(newItemRequest.getAvailable());
        item.setRequest(itemRequest);

        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwnerId(item.getOwner().getId());
        dto.setAvailable(item.getAvailable());
        if (!(item.getRequest() == null)) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }

    public static AdvancedItemDto mapToAdvancedItemDto(Item item, Optional<LocalDateTime> mayBelastBooking, Optional<LocalDateTime> mayBeNextBooking, List<CommentDto> comments) {
        AdvancedItemDto dto = new AdvancedItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setOwnerId(item.getOwner().getId());
        dto.setAvailable(item.getAvailable());

        if (!(item.getRequest() == null)) {
            dto.setRequestId(item.getRequest().getId());
        }

        if (mayBelastBooking.isPresent()) {
            dto.setLastBooking(mayBelastBooking.get());
        } else {
            dto.setLastBooking(null);
        }

        if (mayBeNextBooking.isPresent()) {
            dto.setNextBooking(mayBeNextBooking.get());
        } else {
            dto.setNextBooking(null);
        }

        dto.setComments(comments);

        return dto;
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

    public static ItemCutDto mapToItemCutDto(Item item) {
        ItemCutDto dto = new ItemCutDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}