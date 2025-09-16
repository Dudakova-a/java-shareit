package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * Перехватывает исключения и возвращает структурированные JSON ответы.
 */
@RestControllerAdvice
public class ErrorHandler {
    /**
     * Обрабатывает NotFoundException и возвращает HTTP 404.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает ValidationException и возвращает HTTP 400.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает NoSuchElementException и возвращает HTTP 404.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(final NoSuchElementException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает AccessDeniedException и возвращает HTTP 403.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(final AccessDeniedException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает SecurityException и возвращает HTTP 403.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityException(final SecurityException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает все остальные исключения и возвращает HTTP 500.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }

    /**
     * DTO для ответа с ошибкой.
     * Содержит стандартизированную структуру для всех ошибок.
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        /**
         * Сообщение об ошибке для клиента
         */
        private String error;
    }
}