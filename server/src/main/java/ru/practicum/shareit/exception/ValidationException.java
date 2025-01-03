package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)//400
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
