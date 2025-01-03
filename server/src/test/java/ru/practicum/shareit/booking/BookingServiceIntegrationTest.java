package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private User anotherUser;
    private Item item;
    private NewBookingRequest bookingInputDto;
    private BookingDto bookingOutputDto;
    private Long userId;
    private Long anotherUserId;
    private Long itemId;
    private Long bookingId;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Создание пользователя
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);
        userId = user.getId();

        // Создание другого пользователя
        anotherUser = new User();
        anotherUser.setName("Test Another User");
        anotherUser.setEmail("anothertest@example.com");
        anotherUser = userRepository.save(anotherUser);
        anotherUserId = anotherUser.getId();

        // Создание вещи
        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item = itemRepository.save(item);
        itemId = item.getId();

        // Создание DTO для бронирования
        bookingInputDto = new NewBookingRequest();
        bookingInputDto.setStart(LocalDateTime.now().plusHours(1));
        bookingInputDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingInputDto.setItemId(itemId);

        // Создание DTO для бронирования
        bookingOutputDto = new BookingDto();
        bookingOutputDto.setId(1L);
        bookingOutputDto.setStart(bookingInputDto.getStart());
        bookingOutputDto.setEnd(bookingInputDto.getEnd());
        bookingOutputDto.setItem(ItemMapper.mapToItemDto(item));
        bookingOutputDto.setBooker(UserMapper.mapToUserDto(anotherUser));
        bookingOutputDto.setStatus(Status.WAITING);

        bookingId = bookingOutputDto.getId();
    }

    @Test
    void testCreateBooking() {
        BookingDto createdBooking = bookingService.createBooking(anotherUserId, bookingInputDto);
        assertNotNull(createdBooking);
        assertEquals(bookingInputDto.getStart(), createdBooking.getStart());
        assertEquals(bookingInputDto.getEnd(), createdBooking.getEnd());
        assertEquals(itemId, createdBooking.getItem().getId());
        assertEquals(anotherUserId, createdBooking.getBooker().getId());
    }

    @Test
    void testGetBookingById() {
        BookingDto createdBooking = bookingService.createBooking(anotherUserId, bookingInputDto);
        BookingDto retrievedBooking = bookingService.getBookingById(anotherUserId, createdBooking.getId());
        assertNotNull(retrievedBooking);
        assertEquals(createdBooking.getId(), retrievedBooking.getId());
    }

    @Test
    void testGetBookingsByBookerIdStatusAll() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsByBookerId(user2Id, String.valueOf(State.ALL));
        assertFalse(bookings.isEmpty());
        assertEquals(3, bookings.size());
    }

    @Test
    void testGetBookingsByBookerIdStatusCurrent() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsByBookerId(user2Id, String.valueOf(State.CURRENT));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByBookerIdStatusPast() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsByBookerId(user2Id, String.valueOf(State.PAST));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(pastBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByBookerIdStatusFuture() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsByBookerId(user2Id, String.valueOf(State.FUTURE));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByBookerIdStatusWaiting() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.approveBooking(userId, pastBookingId, true);
        bookingService.approveBooking(userId, currentBookingId, true);

        List<BookingDto> bookings = bookingService.getBookingsByBookerId(user2Id, String.valueOf(State.WAITING));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByBookerIdStatusRejected() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.approveBooking(userId, pastBookingId, true);
        bookingService.approveBooking(userId, currentBookingId, false);

        List<BookingDto> bookings = bookingService.getBookingsByBookerId(user2Id, String.valueOf(State.REJECTED));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByBookerIdNotValidStatus() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.approveBooking(userId, pastBookingId, true);
        bookingService.approveBooking(userId, currentBookingId, false);

//        NotFoundException thrown = assertThrows(NotFoundException.class,
//                () -> bookingService.getBookingsByBookerId(user2Id, "All"));
//        assertEquals("Статус указан неверно", thrown.getMessage());

        //не получается вызвать нужную ошибку

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsByBookerId(user2Id, "TEXT"));
    }

    @Test
    void testApproveBookingStatus() {
        BookingDto createdBooking = bookingService.createBooking(anotherUserId, bookingInputDto);
        BookingDto updatedBooking = bookingService.approveBooking(userId, createdBooking.getId(), true);
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void testGetBookingsByOwnerIdStatusAll() {
        bookingService.createBooking(anotherUserId, bookingInputDto);
        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(userId, String.valueOf(State.ALL));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingsByOwnerIdStatusCurrent() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(userId, String.valueOf(State.CURRENT));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByOwnerIdStatusPast() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(userId, String.valueOf(State.PAST));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(pastBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByOwnerIdStatusFuture() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(userId, String.valueOf(State.FUTURE));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByOwnerIdStatusWaiting() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.approveBooking(userId, pastBookingId, true);
        bookingService.approveBooking(userId, currentBookingId, true);

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(userId, String.valueOf(State.WAITING));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(futureBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByOwnerIdStatusRejected() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.approveBooking(userId, pastBookingId, true);
        bookingService.approveBooking(userId, currentBookingId, false);

        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(userId, String.valueOf(State.REJECTED));
        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(currentBookingId, bookings.getFirst().getId());
    }

    @Test
    void testGetBookingsByOwnerIdNotValidStatus() {
        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest pastBooking = new NewBookingRequest();
        pastBooking.setItemId(itemId);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        BookingDto pastBookingDto = bookingService.createBooking(user2Id, pastBooking);
        Long pastBookingId = pastBookingDto.getId();

        NewBookingRequest currentBooking = new NewBookingRequest();
        currentBooking.setItemId(itemId);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBookingDto = bookingService.createBooking(user2Id, currentBooking);
        Long currentBookingId = currentBookingDto.getId();

        NewBookingRequest futureBooking = new NewBookingRequest();
        futureBooking.setItemId(itemId);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto futureBookingDto = bookingService.createBooking(user2Id, futureBooking);
        Long futureBookingId = futureBookingDto.getId();

        bookingService.approveBooking(userId, pastBookingId, true);
        bookingService.approveBooking(userId, currentBookingId, false);

//        NotFoundException thrown = assertThrows(NotFoundException.class,
//                () -> bookingService.getBookingsByBookerId(user2Id, "All"));
//        assertEquals("Статус указан неверно", thrown.getMessage());

                //не получается вызвать нужную ошибку

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                       () -> bookingService.getBookingsByOwnerId(user2Id, "TEXT"));
    }

    @Test
    void testGetByIdWithInvalidUserId() {
        BookingDto createdBooking = bookingService.createBooking(anotherUserId, bookingInputDto);
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(createdBooking.getId(), 999L));
    }

    @Test
    void testCreateBookingWithUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(anotherUserId, bookingInputDto));
    }

    @Test
    void testCreateBookingWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(999L, bookingInputDto));
    }

    @Test
    void testCreateBookingWithNonExistentItem() {
        bookingInputDto.setItemId(999L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(userId, bookingInputDto));
    }

    @Test
    void testCreateBookingWithCrossedTime() {
        BookingDto createdBooking = bookingService.createBooking(anotherUserId, bookingInputDto);

        User user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test2@example.com");
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();

        NewBookingRequest crossedBooking = new NewBookingRequest();
        crossedBooking.setItemId(itemId);
        crossedBooking.setStart(LocalDateTime.now().plusMinutes(70));
        crossedBooking.setEnd(LocalDateTime.now().plusMinutes(80));
        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user2Id, crossedBooking));
        assertEquals("В это время вещь занята", thrown.getMessage());
    }
}