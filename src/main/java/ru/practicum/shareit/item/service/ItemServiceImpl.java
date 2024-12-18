package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(long ownerId, NewItemRequest newItemRequest) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь c ID " + ownerId + " не найден"));

        ItemRequest itemRequest = requestRepository.findById(newItemRequest.getRequestId());
        Item item = ItemMapper.mapToItem(owner, itemRequest, newItemRequest);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long ownerId, long itemId, UpdateItemRequest request) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь c ID " + ownerId + " не найден"));
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        if (updatedItem.getOwner().getId() != owner.getId()) {
            String message = "У вещи с ID " + itemId + " другой владелец";
            log.error(message);
            throw new NotFoundException(message);
        }
        ItemMapper.updateItemFields(updatedItem, request);
        updatedItem = itemRepository.save(updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public List<AdvancedItemDto> getAllItems(long ownerId) {
        return itemRepository.findAllItemsByOwnerId(ownerId).stream()
                .map(this::loadAdvancedData)
                .toList();
    }

    public AdvancedItemDto loadAdvancedData(Item item) {

        Optional<LocalDateTime> mayBeLastBooking = Optional.empty();
        Optional<LocalDateTime> mayBeNextBooking = Optional.empty();

        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

        return ItemMapper.mapToAdvancedItemDto(item, mayBeLastBooking, mayBeNextBooking, comments);
    }

    @Override
    public AdvancedItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        return loadAdvancedData(item);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentDto createComment(long userId, long itemId, NewCommentRequest newCommentRequest) {
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь c ID " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        List<Booking> pastBookingsList = bookingRepository.findBookingsByBookerIdAndItemIdAndStatusAndEndIsBefore(userId, item.getId(), Status.APPROVED, LocalDateTime.now());
        if (pastBookingsList.isEmpty()) {
            String message = "Пользователь c ID " + userId + " не брал в аренду вещь с ID " + item.getId();
            log.error(message);
            throw new ValidationException(message);
        }
        Comment comment = CommentMapper.mapToComment(author, item, newCommentRequest);
        comment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(comment);
    }
}
