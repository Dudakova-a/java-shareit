package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingWithUserDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingWithUserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingWithUserDto() throws Exception {
        // Создаем связанные объекты
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        // Создаем основной объект
        BookingWithUserDto dto = BookingWithUserDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 2, 10, 0))
                .item(itemDto)
                .booker(userDto)
                .status("APPROVED")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        // Проверяем основные поля
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"2024-01-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2024-01-02T10:00:00\"");
        assertThat(json).contains("\"status\":\"APPROVED\"");

        // Проверяем вложенные объекты
        assertThat(json).contains("\"item\"");
        assertThat(json).contains("\"booker\"");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"name\":\"Test User\"");
    }

    @Test
    void shouldDeserializeBookingWithUserDto() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"start\":\"2024-01-01T10:00:00\"," +
                "\"end\":\"2024-01-02T10:00:00\"," +
                "\"status\":\"APPROVED\"," +
                "\"item\":{" +
                "\"id\":1," +
                "\"name\":\"Test Item\"," +
                "\"description\":\"Test Description\"," +
                "\"available\":true" +
                "}," +
                "\"booker\":{" +
                "\"id\":1," +
                "\"name\":\"Test User\"," +
                "\"email\":\"test@example.com\"" +
                "}" +
                "}";

        BookingWithUserDto dto = objectMapper.readValue(json, BookingWithUserDto.class);

        // Проверяем основные поля
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
        assertThat(dto.getStatus()).isEqualTo("APPROVED");

        // Проверяем вложенные объекты
        assertThat(dto.getItem()).isNotNull();
        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getItem().getName()).isEqualTo("Test Item");
        assertThat(dto.getItem().getAvailable()).isTrue();

        assertThat(dto.getBooker()).isNotNull();
        assertThat(dto.getBooker().getId()).isEqualTo(1L);
        assertThat(dto.getBooker().getName()).isEqualTo("Test User");
        assertThat(dto.getBooker().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleNullFields() throws Exception {
        // Тест с частично заполненным объектом
        BookingWithUserDto dto = BookingWithUserDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 1, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 2, 10, 0))
                .status("WAITING")
                // item и booker остаются null
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
        // Проверяем, что null поля сериализуются как null
        assertThat(json).contains("\"item\":null");
        assertThat(json).contains("\"booker\":null");
    }
}