package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING, APPROVED, REJECTED, CANCELED;
    //WAITING — новое бронирование, ожидает одобрения,
    //APPROVED — бронирование подтверждено владельцем,
    //REJECTED — бронирование отклонено владельцем,
    //CANCELED — бронирование отменено создателем.
}
