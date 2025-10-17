package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        // Given
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "fieldName", "default message"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // When
        Map<String, String> result = errorHandler.handleValidationExceptions(ex);

        // Then
        assertNotNull(result);
        assertEquals("default message", result.get("error"));
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbidden() {
        // Given
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        // When
        ErrorHandler.ErrorResponse result = errorHandler.handleAccessDeniedException(ex);

        // Then
        assertNotNull(result);
        assertEquals("Access denied", result.getError());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        // Given
        NotFoundException ex = new NotFoundException("Not found");

        // When
        ErrorHandler.ErrorResponse result = errorHandler.handleNotFoundException(ex);

        // Then
        assertNotNull(result);
        assertEquals("Not found", result.getError());
    }

    @Test
    void handleNoSuchElementException_ShouldReturnNotFound() {
        // Given
        NoSuchElementException ex = new NoSuchElementException("No such element");

        // When
        ErrorHandler.ErrorResponse result = errorHandler.handleNoSuchElementException(ex);

        // Then
        assertNotNull(result);
        assertEquals("No such element", result.getError());
    }

    @Test
    void handleValidationException_WithConflictMessage_ShouldReturnConflict() {
        // Given
        ValidationException ex = new ValidationException("Email already exists");

        // When
        ResponseEntity<Object> result = errorHandler.handleValidationException(ex);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertTrue(result.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertEquals("Email already exists", body.get("error"));
    }

    @Test
    void handleValidationException_WithAlreadyBooked_ShouldReturnConflict() {
        // Given
        ValidationException ex = new ValidationException("Item already booked");

        // When
        ResponseEntity<Object> result = errorHandler.handleValidationException(ex);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerError() {
        // Given
        RuntimeException ex = new RuntimeException("Unexpected error");

        // When
        ErrorHandler.ErrorResponse result = errorHandler.handleThrowable(ex);

        // Then
        assertNotNull(result);
        assertEquals("Произошла непредвиденная ошибка.", result.getError());
    }

    @Test
    void errorResponse_ShouldHaveCorrectStructure() {
        // Given
        String errorMessage = "Test error message";

        // When
        ErrorHandler.ErrorResponse response = new ErrorHandler.ErrorResponse(errorMessage);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getError());

        // Test setters
        response.setError("New message");
        assertEquals("New message", response.getError());
    }
}