package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_PositiveCase() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);
        User owner = new User(bookerId + 1, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        ItemDto itemDto = new ItemDto(itemId, "item name", "item description", true, owner.getId(), 0);
        long bookingId = 0L;
        Booking bookingToSave = new Booking(bookingId, start, end, item, booker, Status.WAITING);
        BookingDto dto = new BookingDto(bookingId, start, end, itemDto, bookerDto, Status.WAITING);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(bookingToSave)).thenReturn(bookingToSave);

        BookingDto actualDto = bookingService.createBooking(bookerId, request);

        assertEquals(dto, actualDto);
        assertEquals(bookingToSave.getStart(), actualDto.getStart());
        assertEquals(bookingToSave.getEnd(), actualDto.getEnd());
        assertEquals(itemId, actualDto.getItem().getId());
        verify(bookingRepository).save(bookingToSave);
    }

    @Test
    void createBooking_whenBookerNotFound() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookerId, request));

        assertEquals("Пользователь с ID " + bookerId + " не найден", thrown.getMessage());
    }

    @Test
    void createBooking_whenItemNotFound() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookerId, request));

        assertEquals("Вещь с ID " + request.getItemId() + " не найдена", thrown.getMessage());
    }

    @Test
    void createBooking_whenItemNotAvailable() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);
        User owner = new User(bookerId + 1, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, false, null);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookerId, request));

        assertEquals("Вещь недоступна для бронирования", thrown.getMessage());
    }

    @Test
    void createBooking_whenBookerIsOwner() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);
        Item item = new Item(itemId, "item name", "item description", booker, true, null);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookerId, request));

        assertEquals("Нельзя бронировать свою вещь", thrown.getMessage());
    }

    @Test
    void createBooking_whenEndBeforeStart() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 3, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        NewBookingRequest request = new NewBookingRequest(start, end, itemId);
        User owner = new User(bookerId + 1, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookerId, request));

        assertEquals("Начало должно быть раньше конца", thrown.getMessage());
    }

    @Test
    void approveBooking_positiveCase() {
        long ownerId = 4L;
        long bookingId = 6L;
        long bookerId = 8L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        User owner = new User(ownerId, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        ItemDto itemDto = new ItemDto(itemId, "item name", "item description", true, owner.getId(), 0);
        Booking bookingToSave = new Booking(bookingId, start,end, item, booker, Status.WAITING);
        BookingDto dto = new BookingDto(bookingId, start, end, itemDto, bookerDto, Status.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToSave));

        BookingDto actualDto = bookingService.approveBooking(ownerId, bookingId, true);

        assertEquals(dto, actualDto);
        assertEquals(bookingToSave.getStart(), actualDto.getStart());
        assertEquals(bookingToSave.getEnd(), actualDto.getEnd());
        assertEquals(Status.APPROVED, actualDto.getStatus());
        verify(bookingRepository).save(bookingToSave);
    }

    @Test
    void approveBooking_whenBookingNotFound() {
        long ownerId = 4L;
        long bookingId = 6L;
        long bookerId = 8L;
        User booker = new User(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        User owner = new User(ownerId, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        Booking bookingToSave = new Booking(bookingId, start,end, item, booker, Status.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(ownerId, bookingId, true));

        assertEquals("Бронирование c ID " + bookingId + " не найдено", thrown.getMessage());
        verify(bookingRepository, never()).save(bookingToSave);
    }

    @Test
    void approveBooking_whenWrongOwner() {
        long ownerId = 4L;
        long wrongOwnerId = 5L;
        long bookingId = 6L;
        long bookerId = 8L;
        User booker = new User(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        User owner = new User(ownerId, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        Booking bookingToSave = new Booking(bookingId, start,end, item, booker, Status.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToSave));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(wrongOwnerId, bookingId, true));

        assertEquals("У вещи с ID " + itemId + " другой владелец", thrown.getMessage());
        verify(bookingRepository, never()).save(bookingToSave);
    }

    @Test
    void approveBooking_whenWrongStatus() {
        long ownerId = 4L;
        long bookingId = 6L;
        long bookerId = 8L;
        User booker = new User(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        User owner = new User(ownerId, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        Booking bookingToSave = new Booking(bookingId, start,end, item, booker, Status.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToSave));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(ownerId, bookingId, true));

        assertEquals("Можно подтвердить или отменить только бронирование со статусом waiting", thrown.getMessage());
        verify(bookingRepository, never()).save(bookingToSave);
    }

    @Test
    void getBookingById_PositiveCaseUserIsBooker() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        User owner = new User(bookerId + 1, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        ItemDto itemDto = new ItemDto(itemId, "item name", "item description", true, owner.getId(), 0);
        long bookingId = 0L;
        Booking bookingToSave = new Booking(bookingId, start, end, item, booker, Status.WAITING);
        BookingDto dto = new BookingDto(bookingId, start, end, itemDto, bookerDto, Status.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToSave));

        BookingDto actualDto = bookingService.getBookingById(bookerId, bookingId);

        assertEquals(dto, actualDto);
        assertEquals(bookingToSave.getStart(), actualDto.getStart());
        assertEquals(bookingToSave.getEnd(), actualDto.getEnd());
        assertEquals(itemId, actualDto.getItem().getId());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingById_PositiveCaseUserIsOwner() {
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        User owner = new User(bookerId + 1, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        ItemDto itemDto = new ItemDto(itemId, "item name", "item description", true, owner.getId(), 0);
        long bookingId = 0L;
        Booking bookingToSave = new Booking(bookingId, start, end, item, booker, Status.WAITING);
        BookingDto dto = new BookingDto(bookingId, start, end, itemDto, bookerDto, Status.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToSave));

        BookingDto actualDto = bookingService.getBookingById(owner.getId(), bookingId);

        assertEquals(dto, actualDto);
        assertEquals(bookingToSave.getStart(), actualDto.getStart());
        assertEquals(bookingToSave.getEnd(), actualDto.getEnd());
        assertEquals(itemId, actualDto.getItem().getId());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingById_whenBookingNotFound() {
        long userId = 3L;
        long bookingId = 6L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userId, bookingId));

        assertEquals("Бронирование c ID " + bookingId + " не найдено", thrown.getMessage());
    }

    @Test
    void getBookingById_UserNotOwnerNotBooker() {
        long userId = 3L;
        long bookerId = 6L;
        User booker = new User(bookerId, "name", "email");
        UserDto bookerDto = new UserDto(bookerId, "name", "email");
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 19, 30, 15);
        LocalDateTime end = LocalDateTime.of(2024, 7, 2, 19, 30, 15);
        long itemId = 23L;
        User owner = new User(bookerId + 1, "owner name", "owner email");
        Item item = new Item(itemId, "item name", "item description", owner, true, null);
        ItemDto itemDto = new ItemDto(itemId, "item name", "item description", true, owner.getId(), 0);
        long bookingId = 0L;
        Booking bookingToSave = new Booking(bookingId, start, end, item, booker, Status.WAITING);
        BookingDto dto = new BookingDto(bookingId, start, end, itemDto, bookerDto, Status.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToSave));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(userId, bookingId));

        assertEquals("Только владелец вещи и создатель брони могут просматривать данные о бронировании", thrown.getMessage());
    }
}