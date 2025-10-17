package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemForBookingDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemForBookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeItemForBookingDto() throws Exception {
        ItemForBookingDto dto = new ItemForBookingDto();
        dto.setId(1L);
        dto.setName("Test Item");
        dto.setDescription("Test Description");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"description\":\"Test Description\"");

        // Убеждаемся, что нет лишних полей
        assertThat(json).doesNotContain("\"available\"");
        assertThat(json).doesNotContain("\"requestId\"");
        assertThat(json).doesNotContain("\"lastBooking\"");
        assertThat(json).doesNotContain("\"comments\"");
    }

    @Test
    void shouldDeserializeItemForBookingDto() throws Exception {
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Power Drill\"," +
                "\"description\":\"Heavy duty drill\"" +
                "}";

        ItemForBookingDto dto = objectMapper.readValue(json, ItemForBookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Power Drill");
        assertThat(dto.getDescription()).isEqualTo("Heavy duty drill");
    }

    @Test
    void shouldHandlePartialData() throws Exception {
        // Тест с неполными данными (например, без description)
        String json = "{" +
                "\"id\":1," +
                "\"name\":\"Test Item\"" +
                "}";

        ItemForBookingDto dto = objectMapper.readValue(json, ItemForBookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void shouldUseAllArgsConstructor() {
        ItemForBookingDto dto = new ItemForBookingDto(1L, "Test Item", "Test Description");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
    }

    @Test
    void shouldUseNoArgsConstructor() {
        ItemForBookingDto dto = new ItemForBookingDto();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getDescription()).isNull();

        // Проверяем setters
        dto.setId(1L);
        dto.setName("New Item");
        dto.setDescription("New Description");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("New Item");
        assertThat(dto.getDescription()).isEqualTo("New Description");
    }

    @Test
    void shouldHandleNullValues() throws Exception {
        ItemForBookingDto dto = new ItemForBookingDto(null, null, null);

        String json = objectMapper.writeValueAsString(dto);
        ItemForBookingDto result = objectMapper.readValue(json, ItemForBookingDto.class);

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    void shouldHandleEmptyStrings() throws Exception {
        ItemForBookingDto dto = new ItemForBookingDto(1L, "", "");

        String json = objectMapper.writeValueAsString(dto);
        ItemForBookingDto result = objectMapper.readValue(json, ItemForBookingDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEmpty();
        assertThat(result.getDescription()).isEmpty();
    }
}