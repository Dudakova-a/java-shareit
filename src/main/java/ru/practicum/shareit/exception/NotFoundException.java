package ru.practicum.shareit.exception;

/**
 * Исключение для случаев, когда запрашиваемый ресурс не найден.
 * Используется для возвращения HTTP 404 статуса.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}