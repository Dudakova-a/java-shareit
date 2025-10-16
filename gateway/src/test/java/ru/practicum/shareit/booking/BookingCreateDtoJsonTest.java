package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    void shouldDeserializeCorrectly() throws Exception {
        // Given
        String content = "{\"itemId\": 1, \"start\": \"2024-01-01T10:00:00\", \"end\": \"2024-01-02T10:00:00\"}";

        // When
        BookingCreateDto dto = json.parseObject(content);

        // Then
        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
    }

    @Test
    void shouldValidateNotNullConstraints() {
        // Given
        BookingCreateDto dto = new BookingCreateDto(null, null, null);

        // When
        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting("message")
                .contains(
                        "Start date cannot be null",
                        "End date cannot be null",
                        "Item ID cannot be null"
                );
    }

    @Test
    void shouldValidateFutureConstraints() {
        // Given
        BookingCreateDto dto = new BookingCreateDto(
                LocalDateTime.now().minusDays(1), // past start
                LocalDateTime.now().minusDays(1), // past end
                1L
        );

        // When
        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
                .contains(
                        "Start date must be in present or future",
                        "End date must be in future"
                );
    }

    @Test
    void shouldPassValidationForValidDto() {
        // Given
        BookingCreateDto dto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L
        );

        // When
        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }
}