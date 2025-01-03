package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(long requestorId, NewItemRequestRequest request);

    List<ItemRequestWithAnswersDto> getItemRequestsByRequestorId(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId);

    ItemRequestWithAnswersDto getItemRequestByRequestId(long userId, long requestId);
}
