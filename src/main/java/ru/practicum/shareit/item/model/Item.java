package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private User owner;
    private Boolean available;
    private ItemRequest request;
}
