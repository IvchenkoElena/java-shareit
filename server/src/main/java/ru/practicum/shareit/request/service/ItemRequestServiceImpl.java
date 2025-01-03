package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Transactional
    @Override
    public ItemRequestDto createItemRequest(long requestorId, NewItemRequestRequest request) {
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + requestorId + " не найден"));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(requestor, request);
        itemRequest = requestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestWithAnswersDto> getItemRequestsByRequestorId(long requestorId) {
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + requestorId + " не найден"));
        List<ItemRequest> itemRequestsByRequestorIdList = requestRepository.findByRequestorId(requestorId);
        return itemRequestsByRequestorIdList.stream()
                .map(ir -> ItemRequestMapper.mapToItemRequestWithAnswersDto(ir,
                        (itemRepository.findAllItemsByRequestId(ir.getId()).stream().map(ItemMapper::mapToItemCutDto).toList())))
                .sorted(Comparator.comparing(ItemRequestWithAnswersDto::getCreated).reversed())
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        List<ItemRequest> allItemRequestsList = requestRepository.findAllByRequestorIdIsNot(userId);
        return allItemRequestsList.stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    public ItemRequestWithAnswersDto getItemRequestByRequestId(long userId, long requestId) {
        ItemRequest itemRequestByRequestId = requestRepository.findById(requestId);
        if (itemRequestByRequestId == null) {
            throw new NotFoundException("Запрос вещи с ID " + userId + " не найден");
        }
        return ItemRequestMapper.mapToItemRequestWithAnswersDto(itemRequestByRequestId, itemRepository.findAllItemsByRequestId(itemRequestByRequestId.getId()).stream().map(ItemMapper::mapToItemCutDto).toList());
    }
}
