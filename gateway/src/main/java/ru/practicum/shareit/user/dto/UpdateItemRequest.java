package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UpdateItemRequest {
    private String name;
    private String description;
    private Boolean available;


    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return !(available == null);
    }
}
