package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestDtoTest {

    private final Validator validator;

    public ItemRequestDtoTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("Должен создать ItemRequestDto с помощью конструктора по умолчанию")
    void shouldCreateWithDefaultConstructor() {
        // When
        ItemRequestDto dto = new ItemRequestDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getDescription());
        assertNull(dto.getRequestorId());
        assertNull(dto.getCreated());
        assertNull(dto.getItems());
    }

    @Test
    @DisplayName("Должен создать ItemRequestDto с помощью полного конструктора")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        Long id = 1L;
        String description = "Need a power drill";
        Long requestorId = 2L;
        LocalDateTime created = LocalDateTime.now();
        List<ItemRequestDto.ItemResponseDto> items = List.of();

        // When
        ItemRequestDto dto = new ItemRequestDto(id, description, requestorId, created, items);

        // Then
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(requestorId, dto.getRequestorId());
        assertEquals(created, dto.getCreated());
        assertEquals(items, dto.getItems());
    }

    @Test
    @DisplayName("Должен создать ItemRequestDto с помощью конструктора без items")
    void shouldCreateWithConstructorWithoutItems() {
        // Given
        Long id = 1L;
        String description = "Need a hammer";
        Long requestorId = 2L;
        LocalDateTime created = LocalDateTime.now();

        // When
        ItemRequestDto dto = new ItemRequestDto(id, description, requestorId, created);

        // Then
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(requestorId, dto.getRequestorId());
        assertEquals(created, dto.getCreated());
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    @DisplayName("Должен корректно работать с геттерами и сеттерами")
    void shouldWorkWithGettersAndSetters() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        Long id = 1L;
        String description = "Need a saw";
        Long requestorId = 3L;
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        List<ItemRequestDto.ItemResponseDto> items = List.of(
                new ItemRequestDto.ItemResponseDto(1L, "Saw", "Good saw", true, 1L, 2L)
        );

        // When
        dto.setId(id);
        dto.setDescription(description);
        dto.setRequestorId(requestorId);
        dto.setCreated(created);
        dto.setItems(items);

        // Then
        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(requestorId, dto.getRequestorId());
        assertEquals(created, dto.getCreated());
        assertEquals(items, dto.getItems());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    @DisplayName("Должен проходить валидацию при корректных данных")
    void shouldPassValidationWithCorrectData() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Valid description");
        dto.setRequestorId(1L);
        dto.setCreated(LocalDateTime.now().minusHours(1));

        // When
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    @DisplayName("Должен не проходить валидацию при пустом описании")
    void shouldFailValidationWithBlankDescription() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(""); // Пустое описание
        dto.setRequestorId(1L);
        dto.setCreated(LocalDateTime.now());

        // When
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен не проходить валидацию при null описании")
    void shouldFailValidationWithNullDescription() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(null); // Null описание
        dto.setRequestorId(1L);
        dto.setCreated(LocalDateTime.now());

        // When
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен не проходить валидацию при null requestorId")
    void shouldFailValidationWithNullRequestorId() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Valid description");
        dto.setRequestorId(null); // Null requestorId
        dto.setCreated(LocalDateTime.now());

        // When
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("ItemRequest requestor не должен быть null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен не проходить валидацию при null created")
    void shouldFailValidationWithNullCreated() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Valid description");
        dto.setRequestorId(1L);
        dto.setCreated(null); // Null created

        // When
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Дата начала ItemRequest не может быть null", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен не проходить валидацию при будущей дате created")
    void shouldFailValidationWithFutureCreated() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Valid description");
        dto.setRequestorId(1L);
        dto.setCreated(LocalDateTime.now().plusDays(1)); // Будущая дата

        // When
        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Дата начала ItemRequest должна быть в прошлом или настоящем", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен корректно работать с equals и hashCode")
    void shouldWorkWithEqualsAndHashCode() {
        // Given
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        ItemRequestDto dto1 = new ItemRequestDto(1L, "Description", 2L, created);
        ItemRequestDto dto2 = new ItemRequestDto(1L, "Description", 2L, created);
        ItemRequestDto dto3 = new ItemRequestDto(2L, "Other Description", 3L, created);

        // Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("Должен корректно преобразовываться в строку")
    void shouldConvertToStringCorrectly() {
        // Given
        ItemRequestDto dto = new ItemRequestDto(1L, "Test Description", 2L, LocalDateTime.now());

        // When
        String stringRepresentation = dto.toString();

        // Then
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("id=1"));
        assertTrue(stringRepresentation.contains("description=Test Description"));
        assertTrue(stringRepresentation.contains("requestorId=2"));
    }

    @Test
    @DisplayName("Должен создавать ItemResponseDto с помощью конструктора по умолчанию")
    void shouldCreateItemResponseDtoWithDefaultConstructor() {
        // When
        ItemRequestDto.ItemResponseDto responseDto = new ItemRequestDto.ItemResponseDto();

        // Then
        assertNotNull(responseDto);
        assertNull(responseDto.getId());
        assertNull(responseDto.getName());
        assertNull(responseDto.getDescription());
        assertNull(responseDto.getAvailable());
        assertNull(responseDto.getRequestId());
        assertNull(responseDto.getOwnerId());
    }

    @Test
    @DisplayName("Должен создавать ItemResponseDto с помощью полного конструктора")
    void shouldCreateItemResponseDtoWithAllArgsConstructor() {
        // Given
        Long id = 1L;
        String name = "Power Drill";
        String description = "Heavy duty drill";
        Boolean available = true;
        Long requestId = 2L;
        Long ownerId = 3L;

        // When
        ItemRequestDto.ItemResponseDto responseDto = new ItemRequestDto.ItemResponseDto(
                id, name, description, available, requestId, ownerId
        );

        // Then
        assertNotNull(responseDto);
        assertEquals(id, responseDto.getId());
        assertEquals(name, responseDto.getName());
        assertEquals(description, responseDto.getDescription());
        assertEquals(available, responseDto.getAvailable());
        assertEquals(requestId, responseDto.getRequestId());
        assertEquals(ownerId, responseDto.getOwnerId());
    }

    @Test
    @DisplayName("Должен корректно работать с геттерами и сеттерами ItemResponseDto")
    void shouldWorkWithItemResponseDtoGettersAndSetters() {
        // Given
        ItemRequestDto.ItemResponseDto responseDto = new ItemRequestDto.ItemResponseDto();
        Long id = 1L;
        String name = "Hammer";
        String description = "Steel hammer";
        Boolean available = true;
        Long requestId = 2L;
        Long ownerId = 3L;

        // When
        responseDto.setId(id);
        responseDto.setName(name);
        responseDto.setDescription(description);
        responseDto.setAvailable(available);
        responseDto.setRequestId(requestId);
        responseDto.setOwnerId(ownerId);

        // Then
        assertEquals(id, responseDto.getId());
        assertEquals(name, responseDto.getName());
        assertEquals(description, responseDto.getDescription());
        assertEquals(available, responseDto.getAvailable());
        assertEquals(requestId, responseDto.getRequestId());
        assertEquals(ownerId, responseDto.getOwnerId());
    }

    @Test
    @DisplayName("Должен корректно работать с equals и hashCode ItemResponseDto")
    void shouldWorkWithItemResponseDtoEqualsAndHashCode() {
        // Given
        ItemRequestDto.ItemResponseDto dto1 = new ItemRequestDto.ItemResponseDto(1L, "Item", "Desc", true, 1L, 2L);
        ItemRequestDto.ItemResponseDto dto2 = new ItemRequestDto.ItemResponseDto(1L, "Item", "Desc", true, 1L, 2L);
        ItemRequestDto.ItemResponseDto dto3 = new ItemRequestDto.ItemResponseDto(2L, "Other", "Other Desc", false, 2L, 3L);

        // Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    @DisplayName("Должен корректно преобразовываться в строку ItemResponseDto")
    void shouldConvertItemResponseDtoToStringCorrectly() {
        // Given
        ItemRequestDto.ItemResponseDto responseDto = new ItemRequestDto.ItemResponseDto(
                1L, "Test Item", "Test Description", true, 2L, 3L
        );

        // When
        String stringRepresentation = responseDto.toString();

        // Then
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("id=1"));
        assertTrue(stringRepresentation.contains("name=Test Item"));
        assertTrue(stringRepresentation.contains("available=true"));
    }

    @Test
    @DisplayName("Должен корректно работать с коллекцией items")
    void shouldWorkWithItemsCollection() {
        // Given
        ItemRequestDto.ItemResponseDto item1 = new ItemRequestDto.ItemResponseDto(1L, "Item1", "Desc1", true, 1L, 2L);
        ItemRequestDto.ItemResponseDto item2 = new ItemRequestDto.ItemResponseDto(2L, "Item2", "Desc2", false, 1L, 3L);
        List<ItemRequestDto.ItemResponseDto> items = List.of(item1, item2);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Request with items");
        dto.setRequestorId(4L);
        dto.setCreated(LocalDateTime.now().minusDays(1));
        dto.setItems(items);

        // Then
        assertNotNull(dto.getItems());
        assertEquals(2, dto.getItems().size());
        assertEquals("Item1", dto.getItems().get(0).getName());
        assertEquals("Item2", dto.getItems().get(1).getName());
        assertTrue(dto.getItems().get(0).getAvailable());
        assertFalse(dto.getItems().get(1).getAvailable());
    }

    @Test
    @DisplayName("Должен корректно работать с пустой коллекцией items")
    void shouldWorkWithEmptyItemsCollection() {
        // Given
        ItemRequestDto dto = new ItemRequestDto(1L, "Empty items request", 2L, LocalDateTime.now());

        // Then
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    @DisplayName("Должен корректно работать с null коллекцией items")
    void shouldWorkWithNullItemsCollection() {
        // Given
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Null items request");
        dto.setRequestorId(2L);
        dto.setCreated(LocalDateTime.now());
        dto.setItems(null);

        // Then
        assertNull(dto.getItems());
    }
}