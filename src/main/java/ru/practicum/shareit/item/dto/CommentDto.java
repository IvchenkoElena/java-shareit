package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String text;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long itemId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long authorId;

    private String authorName;

    private LocalDateTime created;
}
