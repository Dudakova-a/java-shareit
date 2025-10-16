package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * Перехватывает исключения и возвращает структурированные JSON ответы.
 */
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put("error", errorMessage);
        });
        return errors;
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
     * Обрабатывает NoSuchElementException и возвращает HTTP 404.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(final NoSuchElementException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обрабатывает NotFoundException и возвращает HTTP 404.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
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
     * Обрабатывает ValidationException и возвращает HTTP 400.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex) {
        Map<String, String> errorResponse = Map.of("error", ex.getMessage());

        // Определяем статус по содержанию сообщения
        HttpStatus status = HttpStatus.BAD_REQUEST; // по умолчанию 400

        if (ex.getMessage().contains("already exists") ||
                ex.getMessage().contains("already booked") ||
                ex.getMessage().contains("Email already")) {
            status = HttpStatus.CONFLICT; // 409 для конфликтов
        }

        return new ResponseEntity<>(errorResponse, status);
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
         * Сообщение об ошибке для клиента.
         */
        private String error;
    }
}