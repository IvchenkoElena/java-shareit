package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(long bookerId, NewBookingRequest request) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + bookerId + " не найден"));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new NotFoundException("Вещь с ID " + request.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            String message = "Вещь недоступна для бронирования";
            log.error(message);
            throw new ValidationException(message);
        }

        if (booker.equals(item.getOwner())) {
            String message = "Нельзя бронировать свою вещь";
            log.error(message);
            throw new ValidationException(message);
        }

        //Проверка, что Start раньше End

        if (!request.getEnd().isAfter(request.getStart())) {
            String message = "Начало должно быть раньше конца";
            log.error(message);
            throw new ValidationException(message);
        }

        //Проверка пересечения

        List<Booking> bookingsList = bookingRepository.findBookingsByItem(item);
        if (bookingsList.stream()
                .anyMatch(b -> isTimeCross(b, request))) {
            String message = "В это время вещь занята";
            log.error(message);
            throw new ValidationException(message);
        }

//        List<Booking> crossedBookings = bookingRepository.findBookingsByItemAndStartIsBeforeOrEndIsAfter(item, request.getEnd(), request.getStart());
//        if (!crossedBookings.isEmpty()) {
//            String message = "В это время вещь занята";
//            log.error(message);
//            throw new ValidationException(message);
//        }

        //пыталась сделать запрос с условием, но не получилось


        Booking booking = BookingMapper.mapToBooking(booker, item, request);
        booking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    private boolean isTimeCross(Booking booking, NewBookingRequest request) {
        return request.getStart().isBefore(booking.getEnd()) || request.getEnd().isAfter(booking.getStart());
    }

    @Override
    public BookingDto approveBooking(long ownerId, long bookingId, Boolean approved) {
        //проверка, что бронирование существует
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование c ID " + bookingId + " не найдено"));
        //проверка, что пользователь является хозяином вещи
        if (ownerId != booking.getItem().getOwner().getId()) {
            String message = "У вещи с ID " + booking.getItem().getId() + " другой владелец";
            log.error(message);
            throw new ValidationException(message);
        }
        //проверка, что статус waiting
        if (!booking.getStatus().equals(Status.WAITING)) {
            String message = "Можно подтвердить или отменить только бронирование со статусом waiting";
            log.error(message);
            throw new ValidationException(message);
        }
        //меняем статус
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        //перезаписываем в репозиторий
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        //проверка, что бронирование существует
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование c ID " + bookingId + " не найдено"));
        //Проверяем, что пользователь либо автор бронирования, либо владелец
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            String message = "Недопустимый пользователь";
            log.error(message);
            throw new NotFoundException(message);
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(long bookerId, String state) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + bookerId + " не найден"));
        State currentState = State.valueOf(state);
        List<Booking> bookingsList;

        switch (currentState) {
            case ALL -> bookingsList = bookingRepository.findAllBookingsByBookerId(bookerId);
            case CURRENT ->
                    bookingsList = bookingRepository.findBookingsByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, LocalDateTime.now(), LocalDateTime.now());
            case PAST ->
                    bookingsList = bookingRepository.findBookingsByBookerIdAndEndIsBefore(bookerId, LocalDateTime.now());
            case FUTURE ->
                    bookingsList = bookingRepository.findBookingsByBookerIdAndStartIsAfter(bookerId, LocalDateTime.now());
            case WAITING ->
                    bookingsList = bookingRepository.findAllBookingsByBookerIdAndStatus(bookerId, Status.WAITING);
            case REJECTED ->
                    bookingsList = bookingRepository.findAllBookingsByBookerIdAndStatus(bookerId, Status.REJECTED);
            default -> throw new NotFoundException("Статус указан неверно");
        }

        return bookingsList.stream()
                .map(BookingMapper::mapToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(long ownerId, String state) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + ownerId + " не найден"));
        State currentState = State.valueOf(state);
        List<Booking> bookingsList;

        switch (currentState) {
            case ALL -> bookingsList = bookingRepository.findAllBookingsByItemOwnerId(ownerId);
            case CURRENT ->
                    bookingsList = bookingRepository.findBookingsByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case PAST ->
                    bookingsList = bookingRepository.findBookingsByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now());
            case FUTURE ->
                    bookingsList = bookingRepository.findBookingsByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now());
            case WAITING ->
                    bookingsList = bookingRepository.findAllBookingsByItemOwnerIdAndStatus(ownerId, Status.WAITING);
            case REJECTED ->
                    bookingsList = bookingRepository.findAllBookingsByItemOwnerIdAndStatus(ownerId, Status.REJECTED);
            default -> throw new NotFoundException("Статус указан неверно");
        }

        return bookingsList.stream()
                .map(BookingMapper::mapToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .toList();
    }
}
