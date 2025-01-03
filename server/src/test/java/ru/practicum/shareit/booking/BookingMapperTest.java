package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nextDay = LocalDateTime.now().plusDays(1);

    private final User user = new User(1L, "john.doe@mail.com", "John Doe");
    private final User requestor = new User(2L, "requestor name", "requestor email");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", requestor, LocalDateTime.of(22,8,22, 0, 0, 0));

    private final Item item = new Item(1L, "name", "description", user, Boolean.TRUE, itemRequest);

    private final UserDto userDto = new UserDto(1L, "john.doe@mail.com", "John Doe");
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", Boolean.TRUE, 1L, 1L);

    private final NewBookingRequest newBooking = new NewBookingRequest(now, nextDay, 1L);
    private final BookingDto dto = new BookingDto(1L, now, nextDay, itemDto, userDto, Status.WAITING);
    private final Booking booking = new Booking(1L, now, nextDay, item, user, Status.WAITING);

    @Test
    public void toBookingDtoTest() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
        assertThat(bookingDto, equalTo(dto));
    }

    @Test
    public void toBookingTest() {
        Booking b = BookingMapper.mapToBooking(user, item, newBooking);
        assertThat(b.getStart(), equalTo(booking.getStart()));
        assertThat(b.getEnd(), equalTo(booking.getEnd()));
        assertThat(b.getStatus(), equalTo(booking.getStatus()));
        assertThat(b.getItem(), equalTo(item));
        assertThat(b.getBooker(), equalTo(user));
    }
}