package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.user.dto.NewCommentRequest;
import ru.practicum.shareit.user.dto.UpdateItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, NewItemRequest requestDto) {
        return post("", userId, requestDto);
    }


    public ResponseEntity<Object> updateItem(long userId, long itemId, UpdateItemRequest requestDto) {
        return patch("/" + itemId, requestDto);
    }

    public ResponseEntity<Object> getAllItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search" + "?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, NewCommentRequest requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }
}