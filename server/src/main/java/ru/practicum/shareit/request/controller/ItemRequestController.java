package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody NewItemRequestRequest request) {
        log.info("Вызван эндпоинт создания запроса вещи");
        return itemRequestService.createItemRequest(userId, request);
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getItemRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вызван эндпоинт получения запросов вещей пользователя с ID {}", userId);
        return itemRequestService.getItemRequestsByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вызван эндпоинт получения всех запросов вещей других пользователей");
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getItemRequestByRequestId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                               @PathVariable("requestId") long requestId) {
        log.info("Вызван эндпоинт получения запроса вещи с ID {}", requestId);
        return itemRequestService.getItemRequestByRequestId(userId, requestId);
    }
}
