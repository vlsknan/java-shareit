package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {
    public ValidateException(String error) {
        super(error);
    }
}

