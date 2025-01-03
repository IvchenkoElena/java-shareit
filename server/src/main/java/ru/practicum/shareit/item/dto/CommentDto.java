package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
