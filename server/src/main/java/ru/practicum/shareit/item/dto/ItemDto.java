package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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