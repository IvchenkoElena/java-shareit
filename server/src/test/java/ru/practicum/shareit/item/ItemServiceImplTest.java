package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_whenOwnerFound() {
        long ownerId = 3L;
        long itemId = 0L;
        User owner = new User(ownerId,"name", "email");
        NewItemRequest request = new NewItemRequest("name", "description", true, 0);
        Item itemToSave = new Item(itemId, "name", "description", owner, true, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(itemToSave)).thenReturn(itemToSave);

        ItemDto actualItem = itemService.createItem(ownerId, request);

        assertEquals(itemToSave.getName(), actualItem.getName());
        assertEquals(itemToSave.getDescription(), actualItem.getDescription());
        assertEquals(itemToSave.getAvailable(), actualItem.getAvailable());
        verify(itemRepository).save(itemToSave);
    }

    @Test
    void createItem_whenOwnerNotFound() {
        long ownerId = 5L;
        long itemId = 0L;
        long requestId = 0L;
        User owner = new User(ownerId,"name", "email");
        User requestor = new User(6L, "name6", "email6");
        ItemRequest itemRequest = new ItemRequest(requestId, "descr", requestor, LocalDateTime.now());
        NewItemRequest request = new NewItemRequest("name", "description", true, requestId);
        Item itemToSave = new Item(itemId, "name", "description", owner, true, itemRequest);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> itemService.createItem(ownerId, request));

        assertEquals("Пользователь с ID " + ownerId + " не найден", thrown.getMessage());
        verify(itemRepository, never()).save(itemToSave);
    }

    @Test
    void updateItem_positiveCasewhenOwnerFoundAndItemFound() {
        long ownerId = 3L;
        long itemId = 5L;
        User owner = new User(ownerId,"name", "email");
        Item oldItem = new Item(itemId, "name", "description", owner, true, null);
        UpdateItemRequest request = new UpdateItemRequest("name2", "description2", false);
        Item itemToSave = new Item(itemId, "name2", "description2", owner, false, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(itemToSave)).thenReturn(itemToSave);

        ItemDto actualItem = itemService.updateItem(ownerId, itemId, request);

        assertEquals(itemToSave.getName(), actualItem.getName());
        assertEquals(itemToSave.getDescription(), actualItem.getDescription());
        assertEquals(itemToSave.getAvailable(), actualItem.getAvailable());
        verify(itemRepository).save(itemToSave);
    }

    @Test
    void updateItem_whenOwnerNotFound() {
        long ownerId = 5L;
        long itemId = 0L;
        User owner = new User(ownerId,"name", "email");
        UpdateItemRequest request = new UpdateItemRequest("name2", "description2", false);
        Item itemToSave = new Item(itemId, "name2", "description2", owner, false, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(ownerId, itemId, request));

        assertEquals("Пользователь с ID " + ownerId + " не найден", thrown.getMessage());
        verify(itemRepository, never()).save(itemToSave);
    }

    @Test
    void updateItem_whenItemNotFound() {
        long ownerId = 3L;
        long itemId = 5L;
        User owner = new User(ownerId,"name", "email");
        Item oldItem = new Item(itemId, "name", "description", owner, true, null);
        UpdateItemRequest request = new UpdateItemRequest("name2", "description2", false);
        Item itemToSave = new Item(itemId, "name2", "description2", owner, false, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(ownerId, itemId, request));

        assertEquals("Вещь с ID " + itemId + " не найдена", thrown.getMessage());
        verify(itemRepository, never()).save(itemToSave);
    }

    @Test
    void updateItem_whenWrongOwner() {
        long ownerId = 3L;
        long itemId = 5L;
        User owner = new User(ownerId,"name", "email");
        User wrongOwner = new User(ownerId + 1,"name1", "email1");
        Item oldItem = new Item(itemId, "name", "description", wrongOwner, true, null);
        UpdateItemRequest request = new UpdateItemRequest("name2", "description2", false);
        Item itemToSave = new Item(itemId, "name2", "description2", owner, false, null);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(ownerId, itemId, request));

        assertEquals("У вещи с ID " + itemId + " другой владелец", thrown.getMessage());
        verify(itemRepository, never()).save(itemToSave);
    }

    @Test
    void getAllItems() {
        long ownerId = 3L;
        User owner = new User(ownerId,"name", "email");
        List<CommentDto> comments = new ArrayList<>();
        Item item1 = new Item(5L, "name", "description", owner, true, null);
        AdvancedItemDto dto1 = new AdvancedItemDto(5L, "name", "description", true, null, null, comments, ownerId, 0);
        Item item2 = new Item(8L, "name2", "description2", owner, false, null);
        AdvancedItemDto dto2 = new AdvancedItemDto(8L, "name2", "description2", false, null, null, comments, ownerId, 0);
        List<Item> itemsListToSave = List.of(item1, item2);
        List<AdvancedItemDto> dtoList = List.of(dto1, dto2);
        when(itemRepository.findAllItemsByOwnerId(ownerId)).thenReturn(itemsListToSave);

        List<AdvancedItemDto> actualItemsList = itemService.getAllItems(ownerId);

        assertEquals(dtoList, actualItemsList);
        verify(itemRepository).findAllItemsByOwnerId(ownerId);
    }

    @Test
    void getItemById_whenItemFound() {
        long itemId = 4L;
        long ownerId = 3L;
        User owner = new User(ownerId,"name", "email");
        Item item = new Item(itemId,"name", "description",owner, true, null);
        AdvancedItemDto dto = new AdvancedItemDto(itemId,"name", "description", true, null, null, new ArrayList<>(), ownerId, 0);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        AdvancedItemDto actualDto = itemService.getItemById(itemId);

        assertEquals(dto, actualDto);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemById_whenItemNotFound() {
        long itemId = 4L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(itemId));

        assertEquals("Вещь с ID " + itemId + " не найдена", thrown.getMessage());
        verify(itemRepository).findById(itemId);
    }
}