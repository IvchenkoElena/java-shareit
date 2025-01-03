package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody NewBookingRequest request) {
        log.info("Вызван эндпоинт создания бронирования");
        return bookingService.createBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("bookingId") long bookingId,
                                     @RequestParam("approved") Boolean approved) {
        log.info("Вызван эндпоинт подтверждения бронирования с ID {}", bookingId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("bookingId") long bookingId) {
        log.info("Вызван эндпоинт получения бронирования с ID {}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Вызван эндпоинт получения бронирований пользователя с ID {}", userId);
        return bookingService.getBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Вызван эндпоинт получения бронирований вещей владельца с ID {}", userId);
        return bookingService.getBookingsByOwnerId(userId, state);
    }
}
