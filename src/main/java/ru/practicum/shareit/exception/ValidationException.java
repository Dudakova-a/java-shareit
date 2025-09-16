package ru.practicum.shareit.exception;

/**
 * Исключение для случаев валидации данных.
 * Используется для возвращения HTTP 400 статуса при невалидных данных.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}