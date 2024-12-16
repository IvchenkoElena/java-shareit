package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
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
        User owner = userRepository.findUserById(ownerId);
        if (owner == null) {
            String message = "Пользователь c ID " + ownerId + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        ItemRequest itemRequest = requestRepository.findById(newItemRequest.getRequestId());
        Item item = ItemMapper.mapToItem(owner, itemRequest, newItemRequest);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long ownerId, long itemId, UpdateItemRequest request) {
        User owner = userRepository.findUserById(ownerId);
        if (owner == null) {
            String message = "Пользователь c ID " + ownerId + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        Item updatedItem = itemRepository.findItemById(itemId);
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
        List<Booking> bookingsList = bookingRepository.findBookingsByItem(item);

//Тест ждет здесь null

//        Optional<LocalDateTime> mayBeLastBooking = bookingsList.stream()
//                .map(Booking::getEnd)
//                .filter(e -> e.isBefore(LocalDateTime.now()))
//                .sorted(Comparator.reverseOrder())
//                .findFirst();
//
//        Optional<LocalDateTime> mayBeNextBooking = bookingsList.stream()
//                .map(Booking::getStart)
//                .filter(e -> e.isAfter(LocalDateTime.now()))
//                .findFirst();

        Optional<LocalDateTime> mayBeLastBooking = Optional.empty();
        Optional<LocalDateTime> mayBeNextBooking = Optional.empty();

        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

        return ItemMapper.mapToAdvancedItemDto(item, mayBeLastBooking, mayBeNextBooking, comments);
    }

    @Override
    public AdvancedItemDto getItemById(long itemId) {
        Item item = itemRepository.findItemById(itemId);
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
        User author = userRepository.findUserById(userId);
        if (author == null) {
            String message = "Пользователь c ID " + userId + " не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            String message = "Вещь c ID " + itemId + " не найдена";
            log.error(message);
            throw new NotFoundException(message);
        }
        List<Booking> pastBookingsList = bookingRepository.findPastBookingsByBookerIdAndItmId(userId, item.getId());
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
