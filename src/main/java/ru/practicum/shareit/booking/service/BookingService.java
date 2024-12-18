package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;


import java.util.List;

public interface BookingService {
    BookingDto createBooking(long bookerId, NewBookingRequest request);

    BookingDto approveBooking(long ownerId, long bookingId, Boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getBookingsByBookerId(long bookerId, String state);

    List<BookingDto> getBookingsByOwnerId(long ownerId, String state);
}
