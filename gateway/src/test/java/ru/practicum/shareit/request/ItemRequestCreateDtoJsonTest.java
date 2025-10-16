package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public ItemRequestCreateDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldSerializeItemRequestCreateDto() throws Exception {
        // Given
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a power drill for weekend project");

        // When
        String json = objectMapper.writeValueAsString(createDto);

        // Then
        assertThat(json).contains("\"description\":\"Need a power drill for weekend project\"");
    }

    @Test
    void shouldDeserializeItemRequestCreateDto() throws Exception {
        // Given
        String json = "{\"description\":\"Need a hammer\"}";

        // When
        ItemRequestCreateDto createDto = objectMapper.readValue(json, ItemRequestCreateDto.class);

        // Then
        assertThat(createDto.getDescription()).isEqualTo("Need a hammer");
    }

    @Test
    void shouldValidateItemRequestCreateDto_WithValidDescription() {
        // Given
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Valid description");

        // When
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(createDto);

        // Then
        assertThat(violations).isEmpty();
    }

}