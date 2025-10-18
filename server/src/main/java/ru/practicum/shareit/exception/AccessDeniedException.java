package ru.practicum.shareit.exception;

/**
 * Исключение для случаев, когда пользователь не имеет прав на выполнение операции.
 * Используется для возвращения HTTP 403 статуса.
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}