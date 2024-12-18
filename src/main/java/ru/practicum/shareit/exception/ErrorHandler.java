package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("error", "Ошибка валидации",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("error", "Искомый объект не найден",
                "errorMessage", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(final ConflictException e) {
        return Map.of("error", "Конфликт",
                "errorMessage", e.getMessage());
    }
}