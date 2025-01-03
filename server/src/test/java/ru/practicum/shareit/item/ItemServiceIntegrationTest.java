package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.AdvancedItemDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private NewItemRequest itemInputDto;
    private UpdateItemRequest itemUpdateRequest;
    private ItemDto itemOutputDto;
    private NewCommentRequest commentInputDto;
    private CommentDto commentOutputDto;
    private Long userId;
    private Long itemId;
    private Long itemRequestId;
    private Long bookingId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Test Request");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        itemRequestId = itemRequest.getId();

        itemInputDto = new NewItemRequest();
        itemInputDto.setName("Test Item");
        itemInputDto.setDescription("Test Description");
        itemInputDto.setAvailable(true);
        itemInputDto.setRequestId(itemRequestId);

        itemOutputDto = itemService.createItem(userId, itemInputDto);
        itemId = itemOutputDto.getId();

        commentInputDto = new NewCommentRequest();
        commentInputDto.setText("Test Comment");

        // Создание завершенной аренды
        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(itemId).orElseThrow());
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        booking = bookingRepository.save(booking);
        bookingId = booking.getId();
    }

    @Test
    void testGetAllByUserId() {
        List<AdvancedItemDto> items = itemService.getAllItems(userId);
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(itemOutputDto.getId(), items.get(0).getId());
    }

    @Test
    void testGetById() {
        AdvancedItemDto item = itemService.getItemById(itemId);
        assertNotNull(item);
        assertEquals(itemOutputDto.getId(), item.getId());
        assertEquals(itemOutputDto.getName(), item.getName());
        assertEquals(itemOutputDto.getDescription(), item.getDescription());
        assertEquals(itemOutputDto.getAvailable(), item.getAvailable());
    }

    @Test
    void testFindByText() {
        List<ItemDto> items = itemService.searchItems("Test");
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(itemOutputDto.getId(), items.get(0).getId());
    }

    @Test
    void testCreateItem() {
        NewItemRequest newItemInputDto = new NewItemRequest();
        newItemInputDto.setName("New Test Item");
        newItemInputDto.setDescription("New Test Description");
        newItemInputDto.setAvailable(true);

        ItemDto newItemOutputDto = itemService.createItem(userId, newItemInputDto);
        assertNotNull(newItemOutputDto);
        assertEquals(newItemInputDto.getName(), newItemOutputDto.getName());
        assertEquals(newItemInputDto.getDescription(), newItemOutputDto.getDescription());
        assertEquals(newItemInputDto.getAvailable(), newItemOutputDto.getAvailable());
    }

    @Test
    void testUpdateItem() {
        UpdateItemRequest updatedItemInputDto = new UpdateItemRequest();
        updatedItemInputDto.setName("Updated Test Item");
        updatedItemInputDto.setDescription("Updated Test Description");
        updatedItemInputDto.setAvailable(false);

        ItemDto updatedItemOutputDto = itemService.updateItem(userId, itemId, updatedItemInputDto);
        assertNotNull(updatedItemOutputDto);
        assertEquals(updatedItemInputDto.getName(), updatedItemOutputDto.getName());
        assertEquals(updatedItemInputDto.getDescription(), updatedItemOutputDto.getDescription());
        assertEquals(updatedItemInputDto.getAvailable(), updatedItemOutputDto.getAvailable());
    }

    @Test
    void testUpdateItemWithoutName() {
        UpdateItemRequest updatedItemInputDto = new UpdateItemRequest();
        updatedItemInputDto.setDescription("Updated Test Description");
        updatedItemInputDto.setAvailable(false);

        ItemDto updatedItemOutputDto = itemService.updateItem(userId, itemId, updatedItemInputDto);
        assertNotNull(updatedItemOutputDto);
        assertEquals(itemInputDto.getName(), updatedItemOutputDto.getName());
        assertEquals(updatedItemInputDto.getDescription(), updatedItemOutputDto.getDescription());
        assertEquals(updatedItemInputDto.getAvailable(), updatedItemOutputDto.getAvailable());
    }

    @Test
    void testUpdateItemWithoutDescription() {
        UpdateItemRequest updatedItemInputDto = new UpdateItemRequest();
        updatedItemInputDto.setName("Updated Test Item");
        updatedItemInputDto.setAvailable(false);

        ItemDto updatedItemOutputDto = itemService.updateItem(userId, itemId, updatedItemInputDto);
        assertNotNull(updatedItemOutputDto);
        assertEquals(updatedItemInputDto.getName(), updatedItemOutputDto.getName());
        assertEquals(itemInputDto.getDescription(), updatedItemOutputDto.getDescription());
        assertEquals(updatedItemInputDto.getAvailable(), updatedItemOutputDto.getAvailable());
    }

    @Test
    void testCreateComment() {
        CommentDto comment = itemService.createComment(userId, itemId, commentInputDto);
        assertNotNull(comment);
        assertEquals(commentInputDto.getText(), comment.getText());
    }

    @Test
    void testCreateCommentWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> itemService.createComment(999L, itemId, commentInputDto));
    }

    @Test
    void testCreateCommentWithNonExistentItem() {
        assertThrows(NotFoundException.class, () -> itemService.createComment(userId, 999L, commentInputDto));
    }

    @Test
    void testCreateCommentWithNonBookedItem() {
        // Удаление завершенной аренды для проверки исключения
        bookingRepository.deleteById(bookingId);
        ValidationException thrown = assertThrows(ValidationException.class,
                () -> itemService.createComment(userId, itemId, commentInputDto));
        assertEquals("Пользователь c ID " + userId + " не брал в аренду вещь с ID " + itemId, thrown.getMessage());
    }

    @Test
    void testUpdateItemWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, itemId, itemUpdateRequest));
    }

    @Test
    void testUpdateItemWithNonExistentItem() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, 999L, itemUpdateRequest));
    }

    @Test
    void testUpdateItemWithNonOwnerUser() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser@example.com");
        newUser = userRepository.save(newUser);
        Long newUserId = newUser.getId();

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(newUserId, itemId, itemUpdateRequest));
        assertEquals("У вещи с ID " + itemId + " другой владелец", thrown.getMessage());
    }
}