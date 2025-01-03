package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String name;
    private String description;
    private Boolean available;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long ownerId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long requestId;
}